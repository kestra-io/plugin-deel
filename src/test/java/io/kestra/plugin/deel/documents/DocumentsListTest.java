package io.kestra.plugin.deel.documents;

import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.common.FetchType;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.deel.AbstractDeelTest;
import io.kestra.plugin.deel.MockDeelController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentsListTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private static final String CONTRACT_ID = "5B74FnLM";

    private List buildTask(FetchType fetchType) {
        return List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .contractId(Property.ofValue(CONTRACT_ID))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListDocumentsSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "cb28e9a7-43cf-4f24-be6f-d72991cace2e",
                        "name": "Employment Agreement Contract",
                        "category": "Onboarding & Registration",
                        "created_at": "2025-09-16T11:54:06.763Z",
                        "updated_at": "2025-09-16T11:54:50.168Z",
                        "category_id": "eda410c0-6148-4c72-96fa-8d3b6895bf5d",
                        "category_type": "EMPLOYMENT_AGREEMENT",
                        "category_description": "Core legal contract"
                    },
                    {
                        "id": "eda410c0-6148-4c72-96fa-8d3b6895bf5d",
                        "name": "Visa Application Letter",
                        "category": "Tax & Fiscal",
                        "created_at": "2025-09-16T12:26:47.769Z",
                        "updated_at": "2025-09-16T12:30:01.320Z",
                        "category_id": "cb28e9a7-43cf-4f24-be6f-d72991cace2e",
                        "category_type": "EMPLOYMENT_AGREEMENT",
                        "category_description": "Employer-issued letters"
                    }
                ],
                "has_more": true,
                "next_cursor": "eyJpZCI6ImVkYTQxMGMwIn0",
                "total_count": 42
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .contractId(Property.ofValue(CONTRACT_ID))
            .limit(Property.ofValue(20))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(42L));
        assertThat(output.getRows().get(0).get("name"), is("Employment Agreement Contract"));
        assertThat(output.getRows().get(0).get("category_type"), is("EMPLOYMENT_AGREEMENT"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
        assertThat(MockDeelController.queryParameters.get("limit"), is("20"));
    }

    @Test
    void testListDocumentsPagination() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [], "has_more": false, "next_cursor": null, "total_count": 20}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .contractId(Property.ofValue(CONTRACT_ID))
            .cursor(Property.ofValue("cursor-abc"))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(20L));
        assertThat(MockDeelController.queryParameters.get("cursor"), is("cursor-abc"));
    }

    @Test
    void testListDocumentsEmptyResult() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [], "has_more": false, "next_cursor": null, "total_count": 0}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(0L));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListDocumentsFetchOne() throws Exception {
        MockDeelController.stubResponse("""
            {
                "data": [
                    {"id": "1", "name": "A", "category": "C", "created_at": "2025-01-01T00:00:00Z", "updated_at": "2025-01-01T00:00:00Z", "category_id": "c1", "category_type": "OTHER", "category_description": "d"},
                    {"id": "2", "name": "B", "category": "C", "created_at": "2025-01-01T00:00:00Z", "updated_at": "2025-01-01T00:00:00Z", "category_id": "c1", "category_type": "OTHER", "category_description": "d"}
                ],
                "has_more": false, "next_cursor": null, "total_count": 2
            }
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow().get("id"), is("1"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListDocumentsFetchNone() throws Exception {
        MockDeelController.stubResponse("""
            {
                "data": [{"id": "1", "name": "A"}],
                "has_more": false, "next_cursor": null, "total_count": 1
            }
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.NONE).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListDocumentsStore() throws Exception {
        MockDeelController.stubResponse("""
            {
                "data": [{"id": "1", "name": "A"}],
                "has_more": false, "next_cursor": null, "total_count": 1
            }
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListDocumentsUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListDocumentsForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListDocumentsNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }

    @Test
    void testListDocumentsRateLimited() {
        MockDeelController.stubError(429, "Too Many Requests");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("429"));
    }
}
