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

class ManagersTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private Managers buildTask(FetchType fetchType) {
        return Managers.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListManagersSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "00000000-0000-0000-0000-000000000000",
                        "email": "john.doe@example.com",
                        "last_name": "Doe",
                        "first_name": "John"
                    },
                    {
                        "id": "11111111-1111-1111-1111-111111111111",
                        "email": "jane.smith@example.com",
                        "last_name": "Smith",
                        "first_name": "Jane"
                    }
                ],
                "page": {"total_rows": 50, "offset": 0, "items_per_page": 25}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Managers task = Managers.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(Property.ofValue(25))
            .offset(Property.ofValue(0))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        Managers.Output output = task.run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(50L));
        assertThat(output.getRows().get(0).get("email"), is("john.doe@example.com"));
        assertThat(output.getRows().get(0).get("first_name"), is("John"));
        assertThat(output.getRows().get(1).get("last_name"), is("Smith"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
        assertThat(MockDeelController.queryParameters.get("limit"), is("25"));
        assertThat(MockDeelController.queryParameters.get("offset"), is("0"));
    }

    @Test
    void testListManagersEmptyResult() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [], "page": {"total_rows": 0}}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Managers.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(0L));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListManagersFetchOne() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "first_name": "A"}, {"id": "2", "first_name": "B"}], "page": {"total_rows": 2}}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Managers.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow().get("id"), is("1"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListManagersFetchNone() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "first_name": "A"}], "page": {"total_rows": 1}}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Managers.Output output = buildTask(FetchType.NONE).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListManagersStore() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "first_name": "A"}], "page": {"total_rows": 1}}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Managers.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListManagersUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListManagersForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListManagersRateLimited() {
        MockDeelController.stubError(429, "Too Many Requests");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("429"));
    }
}
