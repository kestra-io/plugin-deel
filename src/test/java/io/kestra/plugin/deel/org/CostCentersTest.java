package io.kestra.plugin.deel.org;

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

class CostCentersTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private static final String LEGAL_ENTITY_ID = "ce652eb3-d49c-3675-8184-d873544eedf7";

    private CostCenters buildTask(FetchType fetchType) {
        return CostCenters.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .legalEntityId(Property.ofValue(LEGAL_ENTITY_ID))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListCostCentersSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": 7654,
                        "created_at": "2025-01-01T00:00:00.000Z",
                        "updated_at": "2025-01-01T00:00:00.000Z",
                        "cost_center_name": "HRIS main",
                        "cost_center_number": "hris-1"
                    },
                    {
                        "id": 7655,
                        "created_at": "2025-01-02T00:00:00.000Z",
                        "updated_at": "2025-01-02T00:00:00.000Z",
                        "cost_center_name": "Engineering",
                        "cost_center_number": "eng-1"
                    }
                ]
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        CostCenters.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows().get(0).get("cost_center_name"), is("HRIS main"));
        assertThat(output.getRows().get(0).get("cost_center_number"), is("hris-1"));
        assertThat(output.getRows().get(1).get("cost_center_name"), is("Engineering"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListCostCentersEmptyResult() throws Exception {
        MockDeelController.stubResponse("""
            {"data": []}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        CostCenters.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListCostCentersFetchOne() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": 1, "cost_center_name": "A"}, {"id": 2, "cost_center_name": "B"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        CostCenters.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow().get("cost_center_name"), is("A"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListCostCentersStore() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": 1, "cost_center_name": "A"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        CostCenters.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListCostCentersUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListCostCentersForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListCostCentersNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }
}
