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

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LegalEntitiesTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private LegalEntities buildTask(FetchType fetchType) {
        return LegalEntities.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListLegalEntitiesSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "b61f6b35-7670-4515-b667-066f02b7b8a0",
                        "name": "Deel US",
                        "phone": "+137867888",
                        "vat_id": "P051709055R",
                        "address": {
                            "zip": "12345",
                            "city": "San Francisco",
                            "state": "Texas",
                            "street": "123 Main St",
                            "country": "US"
                        },
                        "country": "US",
                        "created_at": "2024-01-01T12:00:00Z",
                        "sic_number": "9072",
                        "updated_at": "2024-01-02T12:00:00Z",
                        "archived_at": null,
                        "entity_type": "company",
                        "industry_name": "Marketing Agency",
                        "entity_subtype": "company",
                        "registrationNumber": "GSUGO3B5"
                    },
                    {
                        "id": "c61f6b35-7670-4515-b667-066f02b7b8a1",
                        "name": "Deel UK",
                        "country": "GB",
                        "entity_type": "company"
                    }
                ],
                "page": {"cursor": "cursor123"}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        LegalEntities.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getRows(), notNullValue());
        assertThat(output.getRows().size(), is(2));

        Map<String, Object> first = output.getRows().get(0);
        assertThat(first.get("id"), is("b61f6b35-7670-4515-b667-066f02b7b8a0"));
        assertThat(first.get("name"), is("Deel US"));
        assertThat(first.get("country"), is("US"));
        assertThat(first.get("entity_type"), is("company"));
        assertThat(first.get("address"), notNullValue());

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListLegalEntitiesWithFilters() throws Exception {
        String response = """
            {
                "data": [
                    {"id": "1", "name": "Deel US", "country": "US", "entity_type": "company"}
                ],
                "page": {"cursor": null}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        LegalEntities task = LegalEntities.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .country(Property.ofValue("US"))
            .entityType(Property.ofValue("company"))
            .limit(Property.ofValue(50))
            .cursor(Property.ofValue("cursor123"))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        LegalEntities.Output output = task.run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(MockDeelController.queryParameters.get("country"), is("US"));
        assertThat(MockDeelController.queryParameters.get("type"), is("company"));
        assertThat(MockDeelController.queryParameters.get("limit"), is("50"));
        assertThat(MockDeelController.queryParameters.get("cursor"), is("cursor123"));
    }

    @Test
    void testListLegalEntitiesEmptyResult() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [], "page": {"cursor": null}}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        LegalEntities.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListLegalEntitiesFetchOne() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}, {"id": "2", "name": "B"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        LegalEntities.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow(), notNullValue());
        assertThat(output.getRow().get("id"), is("1"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListLegalEntitiesFetchNone() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        LegalEntities.Output output = buildTask(FetchType.NONE).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getRows(), nullValue());
        assertThat(output.getRow(), nullValue());
    }

    @Test
    void testListLegalEntitiesStore() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        LegalEntities.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListLegalEntitiesUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListLegalEntitiesForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListLegalEntitiesNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }

    @Test
    void testListLegalEntitiesRateLimited() {
        MockDeelController.stubError(429, "Too Many Requests");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("429"));
    }
}
