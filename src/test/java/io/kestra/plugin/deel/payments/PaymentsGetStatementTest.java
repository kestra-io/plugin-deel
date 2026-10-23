package io.kestra.plugin.deel.payments;

import io.kestra.core.models.property.Property;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.deel.AbstractDeelTest;
import io.kestra.plugin.deel.MockDeelController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentsGetStatementTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private GetStatement buildTask(String statementId) {
        return GetStatement.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .paymentStatementId(Property.ofValue(statementId))
            .build();
    }

    @Test
    void testGetStatementSuccess() throws Exception {
        String statementId = "34d7Y48Ymu44g66fghN9F";
        String response = """
            {
                "data": {
                    "id": "%s",
                    "amount": "1500.00",
                    "status": "PROCESSING",
                    "invoices": [
                        {
                            "id": "34d7Y48Ymu44g66fghN9F",
                            "amount": "500.00",
                            "currency": "USD",
                            "label": "INV-2024-3",
                            "is_auto_added": false
                        }
                    ],
                    "amount_due": "1200.00",
                    "payment_currency": "USD",
                    "paid_at": "2022-05-24T09:38:46.235Z",
                    "reference": "DEEL-REF-0001",
                    "created_at": "2022-05-24T09:38:46.235Z",
                    "payment_method": "bank_transfer",
                    "balance_applied": "300.00",
                    "beneficiary_details": {
                        "iban": "GB29NWBK60161331926819",
                        "swift": "NWBKGB2L",
                        "country": "GB",
                        "currency": "USD",
                        "bank_name": "JPMorgan Chase",
                        "sort_code": "04-00-04",
                        "account_holder": "Deel Inc.",
                        "account_number": "1234567890"
                    },
                    "fee_credits_applied": "50.00"
                }
            }
            """.formatted(statementId);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        GetStatement.Output output = buildTask(statementId).run(runContext);

        assertThat(output.getStatement(), notNullValue());
        assertThat(output.getStatement().get("id"), is(statementId));
        assertThat(output.getStatement().get("amount"), is("1500.00"));
        assertThat(output.getStatement().get("status"), is("PROCESSING"));
        assertThat(output.getStatement().get("payment_currency"), is("USD"));
        assertThat(output.getStatement().get("reference"), is("DEEL-REF-0001"));
        assertThat(output.getStatement().get("payment_method"), is("bank_transfer"));

        Object invoices = output.getStatement().get("invoices");
        assertThat(invoices, instanceOf(List.class));
        assertThat((List<?>) invoices, hasSize(1));

        assertThat(output.getStatement().get("beneficiary_details"), notNullValue());

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testGetStatementMinimalFields() throws Exception {
        String statementId = "stmt-minimal";
        String response = """
            {
                "data": {
                    "id": "%s",
                    "amount": "100.00",
                    "status": "PAID",
                    "invoices": [],
                    "amount_due": "0.00",
                    "payment_currency": "USD"
                }
            }
            """.formatted(statementId);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        GetStatement.Output output = buildTask(statementId).run(runContext);

        assertThat(output.getStatement(), notNullValue());
        assertThat(output.getStatement().get("id"), is(statementId));
        assertThat(output.getStatement().get("status"), is("PAID"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetStatementInvoiceDetails() throws Exception {
        String statementId = "stmt-123";
        String response = """
            {
                "data": {
                    "id": "%s",
                    "amount": "2000.00",
                    "status": "PAID",
                    "invoices": [
                        {"id": "inv-1", "amount": "1000.00", "currency": "USD", "label": "INV-1", "is_auto_added": false},
                        {"id": "inv-2", "amount": "1000.00", "currency": "USD", "label": "INV-2", "is_auto_added": true}
                    ],
                    "amount_due": "0.00",
                    "payment_currency": "USD",
                    "created_at": "2024-01-01T00:00:00Z"
                }
            }
            """.formatted(statementId);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        GetStatement.Output output = buildTask(statementId).run(runContext);

        List<Map<String, Object>> invoices = (List<Map<String, Object>>) output.getStatement().get("invoices");
        assertThat(invoices, hasSize(2));
        assertThat(invoices.get(0).get("id"), is("inv-1"));
        assertThat(invoices.get(1).get("is_auto_added"), is(true));
    }

    @Test
    void testGetStatementNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask("missing-id").run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }

    @Test
    void testGetStatementForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask("stmt-1").run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }
}
