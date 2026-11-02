package io.kestra.plugin.deel.contracts;

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

class ContractsGetTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    @Test
    void testGetContractSuccess() throws Exception {
        String contractId = "550e8400-e29b-41d4-a716-446655440000";

        String response = """
            {
                "id": "%s",
                "title": "Contract A",
                "contract_type": "open",
                "status": "active",
                "legal_entity_id": "le-123",
                "team_id": "team-456",
                "start_date": "2024-01-15",
                "end_date": "2025-01-15",
                "created_at": "2024-01-15T10:00:00Z",
                "updated_at": "2024-06-15T10:00:00Z"
            }
            """.formatted(contractId);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        ContractsGet task = ContractsGet.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .contractId(io.kestra.core.models.property.Property.ofValue(contractId))
            .build();

        ContractsGet.Output output = task.run(runContext);

        assertThat(output.getContract(), notNullValue());
        assertThat(output.getContract().get("id"), is(contractId));
        assertThat(output.getContract().get("title"), is("Contract A"));
        assertThat(output.getContract().get("contract_type"), is("open"));
        assertThat(output.getContract().get("status"), is("active"));
        assertThat(output.getContract().get("legal_entity_id"), is("le-123"));
        assertThat(output.getContract().get("team_id"), is("team-456"));
        assertThat(output.getContract().get("start_date"), is("2024-01-15"));
        assertThat(output.getContract().get("end_date"), is("2025-01-15"));
        assertThat(output.getContract().get("created_at"), is("2024-01-15T10:00:00Z"));
        assertThat(output.getContract().get("updated_at"), is("2024-06-15T10:00:00Z"));

        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testGetContractMinimalFields() throws Exception {
        String contractId = "550e8400-e29b-41d4-a716-446655440000";

        String response = """
            {
                "id": "%s",
                "title": "Minimal Contract"
            }
            """.formatted(contractId);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        ContractsGet task = ContractsGet.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .contractId(io.kestra.core.models.property.Property.ofValue(contractId))
            .build();

        ContractsGet.Output output = task.run(runContext);

        assertThat(output.getContract(), notNullValue());
        assertThat(output.getContract().get("id"), is(contractId));
        assertThat(output.getContract().get("title"), is("Minimal Contract"));
    }

    @Test
    void testGetContractHeaderIsSent() throws Exception {
        String contractId = "550e8400-e29b-41d4-a716-446655440000";

        String response = """
            {
                "id": "%s",
                "title": "Contract A",
                "contract_type": "open",
                "status": "active"
            }
            """.formatted(contractId);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        ContractsGet task = ContractsGet.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .contractId(io.kestra.core.models.property.Property.ofValue(contractId))
            .build();

        ContractsGet.Output output = task.run(runContext);

        // Verify X-Version header is sent on the request
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }
}