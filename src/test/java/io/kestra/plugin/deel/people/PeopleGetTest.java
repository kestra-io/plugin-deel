package io.kestra.plugin.deel.people;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.deel.AbstractDeelTest;
import io.kestra.plugin.deel.MockDeelController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PeopleGetTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    @Test
    void testGetPersonSuccess() throws Exception {
        String personId = "550e8400-e29b-41d4-a716-446655440000";

        String personJson = """
            {
                "id": "%s",
                "first_name": "John",
                "last_name": "Doe",
                "full_name": "John Doe",
                "email": "john.doe@example.com",
                "personal_email": "john.personal@example.com",
                "phone": "+1-555-123-4567",
                "timezone": "America/New_York",
                "locale": "en_US",
                "date_of_birth": "1990-05-15",
                "gender": "male",
                "nationality": "US",
                "hiring_status": "active",
                "hiring_type": "employee",
                "job_title": "Senior Software Engineer",
                "department": "Engineering",
                "legal_entity_id": "le-123",
                "legal_entity_name": "Acme Inc.",
                "team_id": "team-456",
                "team_name": "Backend Team",
                "manager_id": "mgr-789",
                "manager_name": "Jane Manager",
                "start_date": "2023-01-15",
                "end_date": null,
                "employments": [
                    {
                        "id": "emp-1",
                        "contract_id": "contract-1",
                        "contract_type": "eor",
                        "status": "active",
                        "job_title": "Senior Software Engineer",
                        "start_date": "2023-01-15"
                    }
                ],
                "address": {
                    "street_address": "123 Main St",
                    "city": "San Francisco",
                    "state": "CA",
                    "postal_code": "94105",
                    "country": "US"
                },
                "bank_account": {
                    "account_holder_name": "John Doe",
                    "bank_name": "Chase Bank",
                    "currency": "USD"
                },
                "created_at": "2023-01-15T10:00:00Z",
                "updated_at": "2024-06-15T10:00:00Z"
            }
            """.formatted(personId);

        MockDeelController.stubResponse(personJson);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        PeopleGet task = PeopleGet.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .personId(io.kestra.core.models.property.Property.ofValue(personId))
            .build();

        PeopleGet.Output output = task.run(runContext);

        assertThat(output.getPerson(), notNullValue());
        assertThat(output.getPerson().get("id"), is(personId));
        assertThat(output.getPerson().get("first_name"), is("John"));
        assertThat(output.getPerson().get("last_name"), is("Doe"));
        assertThat(output.getPerson().get("full_name"), is("John Doe"));
        assertThat(output.getPerson().get("email"), is("john.doe@example.com"));
        assertThat(output.getPerson().get("timezone"), is("America/New_York"));
        assertThat(output.getPerson().get("hiring_status"), is("active"));
        assertThat(output.getPerson().get("job_title"), is("Senior Software Engineer"));
        assertThat(output.getPerson().get("employments"), notNullValue());
        assertThat(output.getPerson().get("address"), notNullValue());
        assertThat(output.getPerson().get("bank_account"), notNullValue());

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
    }

    @Test
    void testGetPersonMinimalFields() throws Exception {
        String personId = "550e8400-e29b-41d4-a716-446655440000";

        String personJson = """
            {
                "id": "%s",
                "first_name": "Jane",
                "last_name": "Smith",
                "email": "jane.smith@example.com"
            }
            """.formatted(personId);

        MockDeelController.stubResponse(personJson);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        PeopleGet task = PeopleGet.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .personId(io.kestra.core.models.property.Property.ofValue(personId))
            .build();

        PeopleGet.Output output = task.run(runContext);

        assertThat(output.getPerson(), notNullValue());
        assertThat(output.getPerson().get("id"), is(personId));
        assertThat(output.getPerson().get("first_name"), is("Jane"));
        assertThat(output.getPerson().get("last_name"), is("Smith"));
        assertThat(output.getPerson().get("email"), is("jane.smith@example.com"));
    }
}