package io.kestra.plugin.deel.timeoff;

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

class TimeoffListTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private static final String PROFILE_ID = "d290f1ee-6c54-4b01-90e6-d701748f0851";

    private List buildTask(FetchType fetchType) {
        return List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .hrisProfileId(Property.ofValue(PROFILE_ID))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListTimeOffsSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                        "amount": 0.5,
                        "is_paid": true,
                        "end_date": "2022-01-05",
                        "created_at": "2022-01-01T00:00:00Z",
                        "start_date": "2022-01-01",
                        "updated_at": "2022-01-01T00:00:00Z",
                        "requested_at": "2022-01-01",
                        "half_end_date": false,
                        "half_start_date": false,
                        "entitlement_unit": "CALENDAR_DAY",
                        "time_off_type_id": "type-1",
                        "reason": "Vacation",
                        "status": "REQUESTED",
                        "description": "Vacation",
                        "contract_oid": "32fk5ds",
                        "deduction_amount": 0.5,
                        "time_off_dailies": [
                            {
                                "id": "daily-1",
                                "date": "2022-01-01",
                                "type": "WORKING_DAY",
                                "amount": 0.5,
                                "created_at": "2022-01-01T00:00:00Z",
                                "updated_at": "2022-01-01T00:00:00Z",
                                "time_off_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                                "description": "Vacation"
                            }
                        ],
                        "recipient_profile": {
                            "hris_profile_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                            "organization_id": "org-1",
                            "client_profile_id": "client-1"
                        },
                        "requester_profile": {
                            "hris_profile_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                            "organization_id": "org-1",
                            "client_profile_id": "client-1"
                        },
                        "time_off_percentage": 0.5,
                        "is_end_date_estimated": false,
                        "other_type_description": null
                    },
                    {
                        "id": "e290f1ee-6c54-4b01-90e6-d701748f0852",
                        "amount": 1.0,
                        "is_paid": false,
                        "end_date": "2022-02-02",
                        "created_at": "2022-02-01T00:00:00Z",
                        "start_date": "2022-02-01",
                        "updated_at": "2022-02-01T00:00:00Z",
                        "requested_at": "2022-02-01",
                        "half_end_date": false,
                        "half_start_date": false,
                        "entitlement_unit": "CALENDAR_DAY",
                        "time_off_type_id": "type-2",
                        "reason": "Sick",
                        "status": "APPROVED"
                    }
                ],
                "page_size": 10,
                "has_next_page": false,
                "count": 2
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .hrisProfileId(Property.ofValue(PROFILE_ID))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows(), notNullValue());
        assertThat(output.getRows().size(), is(2));

        Map<String, Object> first = output.getRows().get(0);
        assertThat(first.get("id"), is("d290f1ee-6c54-4b01-90e6-d701748f0851"));
        assertThat(first.get("status"), is("REQUESTED"));
        assertThat(first.get("reason"), is("Vacation"));
        assertThat(first.get("start_date"), is("2022-01-01"));
        assertThat(first.get("end_date"), is("2022-01-05"));
        assertThat(first.get("is_paid"), is(true));
        assertThat(first.get("time_off_dailies"), notNullValue());
        assertThat(first.get("recipient_profile"), notNullValue());

        Map<String, Object> second = output.getRows().get(1);
        assertThat(second.get("id"), is("e290f1ee-6c54-4b01-90e6-d701748f0852"));
        assertThat(second.get("status"), is("APPROVED"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListTimeOffsWithFilters() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "1",
                        "amount": 1.0,
                        "is_paid": true,
                        "end_date": "2024-06-01",
                        "created_at": "2024-05-01T00:00:00Z",
                        "start_date": "2024-06-01",
                        "updated_at": "2024-05-01T00:00:00Z",
                        "requested_at": "2024-05-01",
                        "half_end_date": false,
                        "half_start_date": false,
                        "entitlement_unit": "CALENDAR_DAY",
                        "time_off_type_id": "type-1",
                        "status": "APPROVED"
                    }
                ],
                "page_size": 10,
                "has_next_page": false,
                "count": 1
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .hrisProfileId(Property.ofValue(PROFILE_ID))
            .status(Property.ofValue("APPROVED"))
            .startDate(Property.ofValue("2024-01-01"))
            .endDate(Property.ofValue("2024-12-31"))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRows().get(0).get("status"), is("APPROVED"));

        assertThat(MockDeelController.queryParameters.get("status"), is("APPROVED"));
        assertThat(MockDeelController.queryParameters.get("start_date"), is("2024-01-01"));
        assertThat(MockDeelController.queryParameters.get("end_date"), is("2024-12-31"));
    }

    @Test
    void testListTimeOffsPagination() throws Exception {
        String response = """
            {
                "data": [],
                "page_size": 10,
                "has_next_page": true,
                "next": "cursor-abc",
                "count": 25
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .hrisProfileId(Property.ofValue(PROFILE_ID))
            .pageSize(Property.ofValue(10))
            .next(Property.ofValue("cursor-abc"))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(25L));
        assertThat(MockDeelController.queryParameters.get("page_size"), is("10"));
        assertThat(MockDeelController.queryParameters.get("next"), is("cursor-abc"));
    }

    @Test
    void testListTimeOffsEmptyResult() throws Exception {
        String response = """
            {
                "data": [],
                "page_size": 10,
                "has_next_page": false,
                "count": 0
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.FETCH).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(0L));
        assertThat(output.getRows(), empty());
    }

    @Test
    void testListTimeOffsFetchOne() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "1",
                        "amount": 1.0,
                        "is_paid": true,
                        "end_date": "2024-06-01",
                        "created_at": "2024-05-01T00:00:00Z",
                        "start_date": "2024-06-01",
                        "updated_at": "2024-05-01T00:00:00Z",
                        "requested_at": "2024-05-01",
                        "half_end_date": false,
                        "half_start_date": false,
                        "entitlement_unit": "CALENDAR_DAY",
                        "time_off_type_id": "type-1",
                        "status": "REQUESTED"
                    },
                    {
                        "id": "2",
                        "amount": 2.0,
                        "is_paid": true,
                        "end_date": "2024-07-02",
                        "created_at": "2024-05-02T00:00:00Z",
                        "start_date": "2024-07-01",
                        "updated_at": "2024-05-02T00:00:00Z",
                        "requested_at": "2024-05-02",
                        "half_end_date": false,
                        "half_start_date": false,
                        "entitlement_unit": "CALENDAR_DAY",
                        "time_off_type_id": "type-1",
                        "status": "REQUESTED"
                    }
                ],
                "page_size": 10,
                "has_next_page": false,
                "count": 2
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.FETCH_ONE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRow(), notNullValue());
        assertThat(output.getRow().get("id"), is("1"));
        assertThat(output.getRows(), nullValue());
    }

    @Test
    void testListTimeOffsFetchNone() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "1",
                        "amount": 1.0,
                        "is_paid": true,
                        "end_date": "2024-06-01",
                        "created_at": "2024-05-01T00:00:00Z",
                        "start_date": "2024-06-01",
                        "updated_at": "2024-05-01T00:00:00Z",
                        "requested_at": "2024-05-01",
                        "half_end_date": false,
                        "half_start_date": false,
                        "entitlement_unit": "CALENDAR_DAY",
                        "time_off_type_id": "type-1",
                        "status": "REQUESTED"
                    }
                ],
                "page_size": 10,
                "has_next_page": false,
                "count": 1
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.NONE).run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getRows(), nullValue());
        assertThat(output.getRow(), nullValue());
    }

    @Test
    void testListTimeOffsStore() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "1",
                        "amount": 1.0,
                        "is_paid": true,
                        "end_date": "2024-06-01",
                        "created_at": "2024-05-01T00:00:00Z",
                        "start_date": "2024-06-01",
                        "updated_at": "2024-05-01T00:00:00Z",
                        "requested_at": "2024-05-01",
                        "half_end_date": false,
                        "half_start_date": false,
                        "entitlement_unit": "CALENDAR_DAY",
                        "time_off_type_id": "type-1",
                        "status": "REQUESTED"
                    }
                ],
                "page_size": 10,
                "has_next_page": false,
                "count": 1
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List.Output output = buildTask(FetchType.STORE).run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getTotal(), is(1L));
        assertThat(output.getUri(), notNullValue());
    }

    @Test
    void testListTimeOffsUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListTimeOffsForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListTimeOffsRateLimited() {
        MockDeelController.stubError(429, "Too Many Requests");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("429"));
    }
}
