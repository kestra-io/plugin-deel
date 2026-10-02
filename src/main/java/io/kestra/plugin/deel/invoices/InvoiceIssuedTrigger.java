package io.kestra.plugin.deel.invoices;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.executions.Execution;
import io.kestra.core.models.conditions.ConditionContext;
import io.kestra.core.models.triggers.PollingTriggerInterface;
import io.kestra.core.models.triggers.TriggerContext;
import io.kestra.core.models.triggers.TriggerOutput;
import io.kestra.core.runners.RunContext;
import io.kestra.plugin.deel.triggers.AbstractDeelTrigger;
import io.kestra.plugin.deel.model.DeelInvoice;
import io.kestra.plugin.deel.model.DeelPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuperBuilder
@Getter
@NoArgsConstructor
@Schema(
    title = "Invoice Issued Trigger",
    description = "Poll GET /rest/invoices with status=all and trigger on newly issued invoices. "
        + "State (watermark and already-seen invoice ids) is persisted in the KV store so polls do not repeat events. "
        + "Invoices without any timestamp are emitted exactly once via the seen-ids set."
)
@Plugin(
    examples = {
        @Example(
            title = "Trigger on newly issued invoices",
            full = true,
            code = """
                id: invoice_events
                namespace: company.team
                tasks:
                  - id: log
                    type: io.kestra.plugin.core.log.Log
                    message: "New invoice {{ trigger.invoice.label }} ({{ trigger.invoice.total }} {{ trigger.invoice.currency }})"
                triggers:
                  - id: watch_invoices
                    type: io.kestra.plugin.deel.invoices.InvoiceIssuedTrigger
                    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
                    interval: PT5M
                """
        )
    }
)
public class InvoiceIssuedTrigger extends AbstractDeelTrigger implements PollingTriggerInterface, TriggerOutput<InvoiceIssuedTrigger.Output> {

    private static final TypeReference<DeelPage<DeelInvoice>> INVOICES_PAGE_TYPE_REF = new TypeReference<>() {};

    @Override
    public Optional<Execution> evaluate(ConditionContext conditionContext, TriggerContext context) throws Exception {
        RunContext runContext = conditionContext.getRunContext();
        Logger logger = runContext.logger();

        TriggerState state = loadState(runContext, context);
        boolean baseline = state.getWatermark() == null && state.getStatuses().isEmpty()
            && state.getSeen().isEmpty() && state.getPending().isEmpty();

        Map<String, Object> params = new HashMap<>();
        params.put("status", "all");
        params.put("limit", 100);
        params.put("offset", 0);

        DeelPage<DeelInvoice> page = request(runContext, "/rest/invoices", "GET", params, INVOICES_PAGE_TYPE_REF);
        List<DeelInvoice> invoices = page != null && page.getData() != null ? page.getData() : new ArrayList<>();

        invoices.sort(Comparator
            .comparing((DeelInvoice invoice) -> eventTime(invoice),
                Comparator.nullsFirst(AbstractDeelTrigger::compareTimestamps))
            .thenComparing(DeelInvoice::getId, Comparator.nullsFirst(String::compareTo)));

        List<String> seen = new ArrayList<>(state.getSeen());
        Set<String> seenSet = new HashSet<>(seen);
        String watermark = state.getWatermark();
        List<Map<String, Object>> pending = new ArrayList<>(state.getPending());

        for (DeelInvoice invoice : invoices) {
            String id = invoice.getId();
            if (id == null) {
                continue;
            }
            String recordTime = eventTime(invoice);
            if (recordTime != null && (watermark == null || compareTimestamps(recordTime, watermark) > 0)) {
                watermark = recordTime;
            }

            if (baseline) {
                seenSet.add(id);
                continue;
            }

            if (!seenSet.contains(id)) {
                seenSet.add(id);
                if (recordTime == null || state.getWatermark() == null || compareTimestamps(recordTime, state.getWatermark()) >= 0) {
                    Map<String, Object> pendingEvent = new HashMap<>();
                    pendingEvent.put("event", "issued");
                    pendingEvent.put("id", id);
                    pendingEvent.put("record", DeelInvoice.toMap(invoice));
                    pending.add(pendingEvent);
                }
            }
        }

        seen = new ArrayList<>(seenSet);
        if (seen.size() > 2000) {
            seen = seen.subList(seen.size() - 1000, seen.size());
        }

        if (baseline && watermark == null) {
            watermark = java.time.Instant.now().toString();
        }

        List<Map<String, Object>> deduped = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();
        for (Map<String, Object> event : pending) {
            String key = event.get("event") + "|" + event.get("id");
            if (seenKeys.add(key)) {
                deduped.add(event);
            }
        }

        if (deduped.isEmpty()) {
            TriggerState next = new TriggerState();
            next.setWatermark(watermark);
            next.setStatuses(state.getStatuses());
            next.setSeen(seen);
            next.setPending(new ArrayList<>());
            saveState(runContext, context, next);
            return Optional.empty();
        }

        Map<String, Object> emitted = deduped.remove(0);

        TriggerState next = new TriggerState();
        next.setWatermark(watermark);
        next.setStatuses(state.getStatuses());
        next.setSeen(seen);
        next.setPending(deduped);
        saveState(runContext, context, next);

        logger.debug("Triggering on invoice {} for {}", emitted.get("event"), emitted.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> invoice = (Map<String, Object>) emitted.get("record");
        return Optional.of(buildExecution(runContext, context, Output.builder()
            .invoice(invoice)
            .build()));
    }

    private String eventTime(DeelInvoice invoice) {
        if (!isBlank(invoice.getIssuedAt())) {
            return invoice.getIssuedAt();
        }
        return invoice.getCreatedAt();
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {

        @Schema(
            title = "Invoice",
            description = "The newly issued invoice."
        )
        private Map<String, Object> invoice;
    }
}
