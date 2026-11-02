package io.kestra.plugin.deel.lookups;

import io.kestra.core.models.property.Property;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.deel.AbstractDeelTest;
import io.kestra.plugin.deel.MockDeelController;
import io.kestra.plugin.deel.lookups.Get.LookupType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LookupsGetTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private Get buildTask(LookupType lookupType) {
        return Get.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .lookupType(Property.ofValue(lookupType))
            .build();
    }

    @Test
    void testGetCountriesSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "code": "US",
                        "name": "United States",
                        "states": [{"code": "NY", "name": "New York"}],
                        "state_type": "state",
                        "eor_support": true,
                        "visa_support": true,
                        "default_currency": "USD"
                    },
                    {
                        "code": "GB",
                        "name": "United Kingdom",
                        "states": [],
                        "eor_support": true,
                        "visa_support": false,
                        "default_currency": "GBP"
                    }
                ]
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Get.Output output = buildTask(LookupType.COUNTRIES).run(runContext);

        assertThat(output.getLookupType(), is(LookupType.COUNTRIES));
        assertThat(output.getSize(), is(2));
        assertThat(output.getRows().get(0).get("code"), is("US"));
        assertThat(output.getRows().get(0).get("name"), is("United States"));
        assertThat(output.getRows().get(0).get("default_currency"), is("USD"));
        assertThat(output.getRows().get(0).get("states"), notNullValue());

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testGetCurrenciesSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {"code": "USD", "name": "United States Dollar"},
                    {"code": "GBP", "name": "British Pound"}
                ]
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Get.Output output = buildTask(LookupType.CURRENCIES).run(runContext);

        assertThat(output.getLookupType(), is(LookupType.CURRENCIES));
        assertThat(output.getSize(), is(2));
        assertThat(output.getRows().get(0).get("code"), is("USD"));
        assertThat(output.getRows().get(1).get("name"), is("British Pound"));
    }

    @Test
    void testGetJobTitlesSuccess() throws Exception {
        String response = """
            {
                "data": [{"id": 1234, "name": "3D Artist"}],
                "page": {"cursor": "next-cursor"}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Get task = Get.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .lookupType(Property.ofValue(LookupType.JOB_TITLES))
            .afterCursor(Property.ofValue("cursor-1"))
            .build();

        Get.Output output = task.run(runContext);

        assertThat(output.getLookupType(), is(LookupType.JOB_TITLES));
        assertThat(output.getSize(), is(1));
        assertThat(output.getRows().get(0).get("name"), is("3D Artist"));
        assertThat(MockDeelController.queryParameters.get("after_cursor"), is("cursor-1"));
    }

    @Test
    void testGetSenioritiesSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {"id": 1, "name": "Junior (Individual Contributor Level 1)", "level": 1},
                    {"id": 8, "name": "C-Level (Executive Level 5)", "level": 8}
                ]
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Get task = Get.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .lookupType(Property.ofValue(LookupType.SENIORITIES))
            .isEorContract(Property.ofValue(false))
            .build();

        Get.Output output = task.run(runContext);

        assertThat(output.getLookupType(), is(LookupType.SENIORITIES));
        assertThat(output.getSize(), is(2));
        assertThat(output.getRows().get(1).get("name"), is("C-Level (Executive Level 5)"));
        assertThat(MockDeelController.queryParameters.get("is_eor_contract"), is("false"));
    }

    @Test
    void testGetLookupEmptyResult() throws Exception {
        MockDeelController.stubResponse("""
            {"data": []}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Get.Output output = buildTask(LookupType.CURRENCIES).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testGetLookupUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(LookupType.COUNTRIES).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testGetLookupForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(LookupType.CURRENCIES).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testGetLookupNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(LookupType.JOB_TITLES).run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }
}
