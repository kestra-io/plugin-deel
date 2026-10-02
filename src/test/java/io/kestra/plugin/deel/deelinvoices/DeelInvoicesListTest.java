package io.kestra.plugin.deel.deelinvoices;

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

class DeelInvoicesListTest extends AbstractDeelTest {

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
    void testListDeelInvoicesSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "rhCTiRd9Mad41RwjsFWw-",
                        "label": "INV-2023-4",
                        "total": "1000",
                        "status": "paid",
                        "currency": "GBP",
                        "created_at": "2022-05-24T09:38:46.235Z"
                    },
                    {
                        "id": "rhCTiRd9Mad41RwjsFWx-",
                        "label": "INV-2023-5",
                        "total": "500",
                        "status": "pending",
                        "currency": "USD",
                        "created_at": "2022-05-25T09:38:46.235Z"
                    }
                ],
                "page": {"offset": 0, "total_rows": 2, "items_per_page": 10}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(Property.ofValue(10))
            .offset(Property.ofValue(0))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows(), notNullValue());
        assertThat(output.getRows().size(), is(2));

        Map<String, Object> first = output.getRows().get(0);
        assertThat(first.get("id"), is("rhCTiRd9Mad41RwjsFWw-"));
        assertThat(first.get("label"), is("INV-2023-4"));
        assertThat(first.get("total"), is("1000"));
        assertThat(first.get("status"), is("paid"));
        assertThat(first.get("currency"), is("GBP"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListDeelInvoicesPagination() throws Exception {
        String response = """
            {
                "data": [],
                "page": {"offset": 10, "total_rows": 20, "items_per_page": 10}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(Property.ofValue(10))
            .offset(Property.ofValue(10))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(MockDeelController.queryParameters.get("limit"), is("10"));
        assertThat(MockDeelController.queryParameters.get("offset"), is("10"));
    }

    @Test
    void testListDeelInvoicesEmptyResult() throws Exception {
        String response = """
            {
                "data": [],
                "page": {"offset": 0, "total_rows": 0, "items_per_page": 10}
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
    void testListDeelInvoicesFetchOne() throws Exception {
        String response = """
            {
                "data": [
                    {"id": "1", "label": "INV-1", "total": "100", "status": "paid", "currency": "USD"}
                ],
                "page": {"offset": 0, "total_rows": 1, "items_per_page": 10}
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
    void testListDeelInvoicesFetchNone() throws Exception {
        String response = """
            {
                "data": [{"id": "1", "label": "INV-1", "total": "100", "status": "paid", "currency": "USD"}],
                "page": {"offset": 0, "total_rows": 1, "items_per_page": 10}
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
    void testListDeelInvoicesUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListDeelInvoicesForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }
}
