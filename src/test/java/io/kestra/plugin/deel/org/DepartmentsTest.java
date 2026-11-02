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

class DepartmentsTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private Departments buildTask(FetchType fetchType) {
        return Departments.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListDepartmentsSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {"id": "c1f4d8ce-66b9-4b98-9054-815237118cbc", "name": "Engineering", "parent": null},
                    {"id": "facc9050-f4c9-4250-bc95-b7584e411a00", "name": "Software Development", "parent": "1"}
                ]
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Departments.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows().get(0).get("name"), is("Engineering"));
        assertThat(output.getRows().get(1).get("parent"), is("1"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListDepartmentsEmptyResult() throws Exception {
        MockDeelController.stubResponse("""
            {"data": []}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Departments.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListDepartmentsFetchOne() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}, {"id": "2", "name": "B"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Departments.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow().get("id"), is("1"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListDepartmentsFetchNone() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Departments.Output output = buildTask(FetchType.NONE).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListDepartmentsStore() throws Exception {
        MockDeelController.stubResponse("""
            {"data": [{"id": "1", "name": "A"}]}
            """);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Departments.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListDepartmentsUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListDepartmentsForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListDepartmentsNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask(FetchType.FETCH).run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }
}
