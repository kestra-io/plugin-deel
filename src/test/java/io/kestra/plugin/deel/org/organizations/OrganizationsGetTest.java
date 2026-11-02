package io.kestra.plugin.deel.org.organizations;

import io.kestra.core.models.property.Property;
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

class OrganizationsGetTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private Get buildTask() {
        return Get.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .build();
    }

    @Test
    void testGetOrganizationSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "00000000-0000-0000-0000-000000000000",
                        "name": "Example Organization"
                    }
                ]
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Get.Output output = buildTask().run(runContext);

        assertThat(output.getOrganization(), notNullValue());
        assertThat(output.getOrganization().get("id"), is("00000000-0000-0000-0000-000000000000"));
        assertThat(output.getOrganization().get("name"), is("Example Organization"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testGetOrganizationNotFound() {
        MockDeelController.stubError(404, "Not Found");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask().run(runContext));
        assertThat(e.getMessage(), containsString("404"));
    }

    @Test
    void testGetOrganizationUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask().run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testGetOrganizationForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        Exception e = assertThrows(Exception.class, () -> buildTask().run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }
}
