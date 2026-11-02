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

class TeamsTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private Teams buildTask(FetchType fetchType) {
        return Teams.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListTeamsSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {"id": "00000000-0000-0000-0000-000000000000", "name": "Engineering"},
                    {"id": "11111111-1111-1111-1111-111111111111", "name": "Marketing"}
                ]
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Teams.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows().get(0).get("name"), is("Engineering"));
        assertThat(output.getRows().get(1).get("id"), is("11111111-1111-1111-1111-111111111111"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListTeamsEmptyResult() throws Exception {
        MockDeelController.stubResponse("""
            {"data": []}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Teams.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListTeamsFetchOne() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}, {"id": "2", "name": "B"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Teams.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow().get("name"), is("A"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListTeamsStore() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Teams.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListTeamsUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListTeamsForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListTeamsNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }
}
