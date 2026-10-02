package io.kestra.plugin.deel.people;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kestra.core.models.tasks.common.FetchType;
import io.kestra.core.runners.RunContext;
import io.kestra.core.runners.RunContextFactory;
import io.kestra.plugin.deel.AbstractDeelTest;
import io.kestra.plugin.deel.MockDeelController;
import io.kestra.plugin.deel.model.DeelPage;
import io.kestra.plugin.deel.model.DeelPerson;
import io.kestra.plugin.deel.model.DeelPagination;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PeopleListTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    @Test
    void testListPeopleSuccess() throws Exception {
        String person1Json = """
            {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "first_name": "John",
                "last_name": "Doe",
                "full_name": "John Doe",
                "email": "john.doe@example.com",
                "hiring_status": "active",
                "hiring_type": "employee",
                "job_title": "Software Engineer",
                "timezone": "America/New_York",
                "created_at": "2024-01-15T10:00:00Z",
                "updated_at": "2024-06-15T10:00:00Z"
            }
            """;

        String person2Json = """
            {
                "id": "550e8400-e29b-41d4-a716-446655440001",
                "first_name": "Jane",
                "last_name": "Smith",
                "full_name": "Jane Smith",
                "email": "jane.smith@example.com",
                "hiring_status": "active",
                "hiring_type": "contractor",
                "job_title": "Designer",
                "timezone": "Europe/London",
                "created_at": "2024-02-01T10:00:00Z",
                "updated_at": "2024-06-15T10:00:00Z"
            }
            """;

        String response = """
            {
                "data": [%s, %s],
                "page": {
                    "offset": 0,
                    "total_rows": 2,
                    "items_per_page": 25,
                    "cursor": null
                }
            }
            """.formatted(person1Json, person2Json);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        PeopleList task = PeopleList.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(io.kestra.core.models.property.Property.ofValue(25))
            .offset(io.kestra.core.models.property.Property.ofValue(0))
            .fetchType(io.kestra.core.models.property.Property.ofValue(FetchType.FETCH))
            .build();

        PeopleList.Output output = task.run(runContext);

        // Verify basic response
        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows(), notNullValue());
        assertThat(output.getRows().size(), is(2));

        // Verify the data structure - rows should contain proper DeelPerson fields
        Map<String, Object> firstPerson = output.getRows().get(0);
        assertThat(firstPerson.get("id"), is("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(firstPerson.get("first_name"), is("John"));
        assertThat(firstPerson.get("last_name"), is("Doe"));
        assertThat(firstPerson.get("full_name"), is("John Doe"));
        assertThat(firstPerson.get("email"), is("john.doe@example.com"));
        assertThat(firstPerson.get("hiring_status"), is("active"));
        assertThat(firstPerson.get("hiring_type"), is("employee"));
        assertThat(firstPerson.get("job_title"), is("Software Engineer"));
        assertThat(firstPerson.get("timezone"), is("America/New_York"));

        // Verify second person
        Map<String, Object> secondPerson = output.getRows().get(1);
        assertThat(secondPerson.get("id"), is("550e8400-e29b-41d4-a716-446655440001"));
        assertThat(secondPerson.get("first_name"), is("Jane"));
        assertThat(secondPerson.get("last_name"), is("Smith"));
        assertThat(secondPerson.get("full_name"), is("Jane Smith"));
    }

    @Test
    void testListPeopleFetchOne() throws Exception {
        String person1Json = """
            {
                "id": "1",
                "first_name": "John",
                "last_name": "Doe"
            }
            """;

        String person2Json = """
            {
                "id": "2",
                "first_name": "Jane",
                "last_name": "Smith"
            }
            """;

        String response = """
            {
                "data": [%s, %s],
                "page": {
                    "offset": 0,
                    "total_rows": 2,
                    "items_per_page": 25
                }
            }
            """.formatted(person1Json, person2Json);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        PeopleList task = PeopleList.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(io.kestra.core.models.property.Property.ofValue(FetchType.FETCH_ONE))
            .build();

        PeopleList.Output output = task.run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow(), notNullValue());
        assertThat(output.getRow().get("id"), is("1"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListPeopleFetchNone() throws Exception {
        String personJson = """
            {
                "id": "1",
                "first_name": "John",
                "last_name": "Doe"
            }
            """;

        String response = """
            {
                "data": [%s],
                "page": {
                    "offset": 0,
                    "total_rows": 1,
                    "items_per_page": 25
                }
            }
            """.formatted(personJson);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        PeopleList task = PeopleList.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(io.kestra.core.models.property.Property.ofValue(FetchType.NONE))
            .build();

        PeopleList.Output output = task.run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getRows(), nullValue());
        assertThat(output.getRow(), nullValue());
        assertThat(output.getUri(), nullValue());
    }

    @Test
    void testListPeopleEmptyResult() throws Exception {
        String response = """
            {
                "data": [],
                "page": {
                    "offset": 0,
                    "total_rows": 0,
                    "items_per_page": 25
                }
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        PeopleList task = PeopleList.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(io.kestra.core.models.property.Property.ofValue(FetchType.FETCH))
            .build();

        PeopleList.Output output = task.run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(0L));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListPeopleWithFilters() throws Exception {
        String personJson = """
            {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "first_name": "John",
                "last_name": "Doe",
                "full_name": "John Doe",
                "email": "john.doe@example.com",
                "hiring_status": "active",
                "hiring_type": "employee",
                "job_title": "Software Engineer",
                "timezone": "America/New_York"
            }
            """;

        String response = """
            {
                "data": [%s],
                "page": {
                    "offset": 0,
                    "total_rows": 1,
                    "items_per_page": 25
                }
            }
            """.formatted(personJson);

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        PeopleList task = PeopleList.builder()
            .apiToken(io.kestra.core.models.property.Property.ofValue("test-token"))
            .baseUrl(io.kestra.core.models.property.Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .hiringStatus(io.kestra.core.models.property.Property.ofValue("active"))
            .search(io.kestra.core.models.property.Property.ofValue("John"))
            .fetchType(io.kestra.core.models.property.Property.ofValue(FetchType.FETCH))
            .build();

        PeopleList.Output output = task.run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRows().get(0).get("first_name"), is("John"));

        assertThat(MockDeelController.queryParameters.get("hiring_status"), is("active"));
        assertThat(MockDeelController.queryParameters.get("search"), is("John"));
    }
}