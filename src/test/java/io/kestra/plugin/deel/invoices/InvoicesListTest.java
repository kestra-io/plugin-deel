package io.kestra.plugin.deel.invoices;

import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.common.FetchType;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.deel.AbstractDeelTest;
import io.kestra.plugin.deel.MockDeelController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvoicesListTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private List buildTask(FetchType fetchType) {
        return List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListInvoicesSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "GqaY5BHQJUAM2Jg9kq2j2",
                        "label": "INV-2023-4",
                        "total": "1000",
                        "status": "paid",
                        "vat_id": "123456789",
                        "paid_at": "2022-05-24T09:38:46.235Z",
                        "currency": "GBP",
                        "due_date": "2022-05-24T09:38:46.235Z",
                        "issued_at": "2022-05-24T09:38:46.235Z",
                        "vat_total": "210",
                        "created_at": "2022-05-24T09:38:46.235Z",
                        "is_overdue": true,
                        "contract_id": "37nex2x",
                        "vat_percentage": "21",
                        "amount": "1000",
                        "deel_fee": "10",
                        "recipient_legal_entity_id": "2c275e07-4846-490e-b2e4-6e84139032c3"
                    },
                    {
                        "id": "GqaY5BHQJUAM2Jg9kq2j3",
                        "label": "INV-2023-5",
                        "total": "2000",
                        "status": "paid",
                        "currency": "USD",
                        "contract_id": "37nex2y",
                        "created_at": "2022-05-25T09:38:46.235Z"
                    }
                ],
                "page": {
                    "offset": 0,
                    "total_rows": 2,
                    "items_per_page": 25,
                    "cursor": null
                }
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(Property.ofValue(25))
            .offset(Property.ofValue(0))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows(), notNullValue());
        assertThat(output.getRows().size(), is(2));

        Map<String, Object> first = output.getRows().get(0);
        assertThat(first.get("id"), is("GqaY5BHQJUAM2Jg9kq2j2"));
        assertThat(first.get("label"), is("INV-2023-4"));
        assertThat(first.get("total"), is("1000"));
        assertThat(first.get("status"), is("paid"));
        assertThat(first.get("currency"), is("GBP"));
        assertThat(first.get("contract_id"), is("37nex2x"));
        assertThat(first.get("vat_total"), is("210"));
        assertThat(first.get("deel_fee"), is("10"));

        Map<String, Object> second = output.getRows().get(1);
        assertThat(second.get("id"), is("GqaY5BHQJUAM2Jg9kq2j3"));
        assertThat(second.get("currency"), is("USD"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListInvoicesWithFilters() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "inv-1",
                        "label": "INV-2024-1",
                        "total": "500",
                        "status": "paid",
                        "currency": "USD",
                        "contract_id": "contract-1",
                        "issued_at": "2024-03-01T00:00:00Z",
                        "created_at": "2024-03-01T00:00:00Z"
                    }
                ],
                "page": {"offset": 0, "total_rows": 1, "items_per_page": 25}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .status(Property.ofValue("all"))
            .contractId(Property.ofValue("contract-1"))
            .issuedFrom(Property.ofValue("2024-01-01"))
            .issuedTo(Property.ofValue("2024-12-31"))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRows().get(0).get("contract_id"), is("contract-1"));

        assertThat(MockDeelController.queryParameters.get("status"), is("all"));
        assertThat(MockDeelController.queryParameters.get("contract_id"), is("contract-1"));
        assertThat(MockDeelController.queryParameters.get("issued_from_date"), is("2024-01-01"));
        assertThat(MockDeelController.queryParameters.get("issued_to_date"), is("2024-12-31"));
    }

    @Test
    void testListInvoicesPagination() throws Exception {
        String response = """
            {
                "data": [],
                "page": {"offset": 25, "total_rows": 25, "items_per_page": 25, "cursor": "next-cursor"}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(Property.ofValue(25))
            .offset(Property.ofValue(25))
            .cursor(Property.ofValue("abc123"))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(MockDeelController.queryParameters.get("limit"), is("25"));
        assertThat(MockDeelController.queryParameters.get("offset"), is("25"));
        assertThat(MockDeelController.queryParameters.get("cursor"), is("abc123"));
    }

    @Test
    void testListInvoicesEmptyResult() throws Exception {
        String response = """
            {
                "data": [],
                "page": {"offset": 0, "total_rows": 0, "items_per_page": 25}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(0L));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListInvoicesFetchOne() throws Exception {
        String response = """
            {
                "data": [
                    {"id": "1", "label": "INV-1", "total": "100", "status": "paid", "currency": "USD"},
                    {"id": "2", "label": "INV-2", "total": "200", "status": "paid", "currency": "USD"}
                ],
                "page": {"offset": 0, "total_rows": 2, "items_per_page": 25}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow(), notNullValue());
        assertThat(output.getRow().get("id"), is("1"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListInvoicesFetchNone() throws Exception {
        String response = """
            {
                "data": [{"id": "1", "label": "INV-1", "total": "100", "status": "paid", "currency": "USD"}],
                "page": {"offset": 0, "total_rows": 1, "items_per_page": 25}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.NONE).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getRows(), nullValue());
        assertThat(output.getRow(), nullValue());
    }

    @Test
    void testListInvoicesStore() throws Exception {
        String response = """
            {
                "data": [{"id": "1", "label": "INV-1", "total": "100", "status": "paid", "currency": "USD"}],
                "page": {"offset": 0, "total_rows": 1, "items_per_page": 25}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListInvoicesUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListInvoicesForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }
}
