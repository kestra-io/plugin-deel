package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel Invoice")
public class DeelInvoice {

    @Schema(description = "Unique identifier of the invoice")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Label or reference number of the invoice")
    @JsonProperty("label")
    private String label;

    @Schema(description = "Total invoice amount, including fees and VAT")
    @JsonProperty("total")
    private String total;

    @Schema(description = "Current status of the invoice (pending, paid, processing, credited, refunded)")
    @JsonProperty("status")
    private String status;

    @Schema(description = "VAT identification number related to the invoice")
    @JsonProperty("vat_id")
    private String vatId;

    @Schema(description = "Date/time when the invoice was paid (ISO-8601). May be null or empty.")
    @JsonProperty("paid_at")
    private String paidAt;

    @Schema(description = "Three-letter currency code for the invoice")
    @JsonProperty("currency")
    private String currency;

    @Schema(description = "Date and time when the invoice is due (ISO-8601)")
    @JsonProperty("due_date")
    private String dueDate;

    @Schema(description = "Date and time when the invoice was issued (ISO-8601)")
    @JsonProperty("issued_at")
    private String issuedAt;

    @Schema(description = "Total amount of VAT charged on the invoice")
    @JsonProperty("vat_total")
    private String vatTotal;

    @Schema(description = "Date and time when the invoice was created (ISO-8601)")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Indicates whether the invoice is overdue")
    @JsonProperty("is_overdue")
    private Boolean overdue;

    @Schema(description = "Unique identifier of the related contract")
    @JsonProperty("contract_id")
    private String contractId;

    @Schema(description = "Percentage of VAT charged on the invoice")
    @JsonProperty("vat_percentage")
    private String vatPercentage;

    @Schema(description = "Billed amount of the invoice")
    @JsonProperty("amount")
    private String amount;

    @Schema(description = "Fee charged by Deel")
    @JsonProperty("deel_fee")
    private String deelFee;

    @Schema(description = "Unique identifier for the recipient legal entity")
    @JsonProperty("recipient_legal_entity_id")
    private String recipientLegalEntityId;

    /**
     * Shared mapping used by both the invoices List task and the InvoiceIssuedTrigger,
     * so trigger outputs match task outputs.
     */
    public static Map<String, Object> toMap(DeelInvoice invoice) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", invoice.getId());
        map.put("label", invoice.getLabel());
        map.put("total", invoice.getTotal());
        map.put("status", invoice.getStatus());
        map.put("vat_id", invoice.getVatId());
        map.put("paid_at", invoice.getPaidAt());
        map.put("currency", invoice.getCurrency());
        map.put("due_date", invoice.getDueDate());
        map.put("issued_at", invoice.getIssuedAt());
        map.put("vat_total", invoice.getVatTotal());
        map.put("created_at", invoice.getCreatedAt());
        map.put("is_overdue", invoice.getOverdue());
        map.put("contract_id", invoice.getContractId());
        map.put("vat_percentage", invoice.getVatPercentage());
        map.put("amount", invoice.getAmount());
        map.put("deel_fee", invoice.getDeelFee());
        map.put("recipient_legal_entity_id", invoice.getRecipientLegalEntityId());
        return map;
    }
}
