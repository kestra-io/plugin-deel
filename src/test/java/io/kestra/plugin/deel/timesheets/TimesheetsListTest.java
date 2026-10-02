package io.kestra.plugin.deel.timesheets;

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

class TimesheetsListTest extends AbstractDeelTest {

    @BeforeAll
    static void startServer() {
    }

    @AfterAll
    static void stopServer() {
    }

    private List buildTask(FetchType fetchType) {
        return List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .fetchType(Property.ofValue(fetchType))
            .build();
    }

    @Test
    void testListTimesheetsSuccess() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "a3f1c9d2-4b7e-4f8a-9c3d-2e5b7f9a1c2d",
                        "type": "work",
                        "status": "approved",
                        "contract": {
                            "id": "c7d9e8f1-2a3b-4c5d-8e9f-0a1b2c3d4e5f",
                            "type": "ongoing_time_based",
                            "title": "Senior Software Engineer Contract"
                        },
                        "quantity": 8,
                        "worksheet": {"days": 1, "hours": 4, "weeks": 0, "minutes": 30},
                        "created_at": "2024-06-15T09:30:00Z",
                        "description": "Timesheet for week 24, 2024",
                        "reported_by": {"id": "d4e5f6a7-b8c9-4d0e-9f1a-2b3c4d5e6f7a", "full_name": "John Smith"},
                        "total_amount": "1200.0000",
                        "currency_code": "GBP",
                        "date_submitted": "2024-06-22T10:00:00Z",
                        "scale": "custom",
                        "attachment": {"key": "timesheet-attachments/2024/06/15/report.pdf", "filename": "June_Timesheet_Report.pdf"},
                        "reviewed_by": {
                            "id": "e7f8a9b0-c1d2-4e3f-8a9b-0c1d2e3f4a5b",
                            "remarks": "Approved with no issues",
                            "reviewed_at": "2024-06-16T14:45:00Z",
                            "full_name": "Jane Smith"
                        },
                        "custom_scale": "hourly",
                        "payment_cycle": {"end_date": "2024-06-21T23:59:59Z", "start_date": "2024-06-15T00:00:00Z"},
                        "hourly_report_preset": {
                            "id": "preset-12345",
                            "rate": 150,
                            "title": "Standard Hourly Rate",
                            "description": "Standard hourly rate for software engineering tasks"
                        }
                    },
                    {
                        "id": "b3f1c9d2-4b7e-4f8a-9c3d-2e5b7f9a1c2e",
                        "type": "work",
                        "status": "pending",
                        "contract": {
                            "id": "c7d9e8f1-2a3b-4c5d-8e9f-0a1b2c3d4e5f",
                            "type": "ongoing_time_based",
                            "title": "Senior Software Engineer Contract"
                        },
                        "quantity": 4,
                        "created_at": "2024-06-16T09:30:00Z",
                        "description": "Follow-up work",
                        "reported_by": {"id": "d4e5f6a7-b8c9-4d0e-9f1a-2b3c4d5e6f7a"},
                        "total_amount": "600.0000",
                        "currency_code": "GBP",
                        "date_submitted": "2024-06-23T10:00:00Z"
                    }
                ],
                "page": {"total_rows": 2}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(Property.ofValue(25))
            .offset(Property.ofValue(0))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(2));
        assertThat(output.getTotal(), is(2L));
        assertThat(output.getRows(), notNullValue());
        assertThat(output.getRows().size(), is(2));

        Map<String, Object> first = output.getRows().get(0);
        assertThat(first.get("id"), is("a3f1c9d2-4b7e-4f8a-9c3d-2e5b7f9a1c2d"));
        assertThat(first.get("status"), is("approved"));
        assertThat(first.get("description"), is("Timesheet for week 24, 2024"));
        assertThat(first.get("total_amount"), is("1200.0000"));
        assertThat(first.get("currency_code"), is("GBP"));
        assertThat(first.get("contract"), notNullValue());
        assertThat(first.get("worksheet"), notNullValue());
        assertThat(first.get("reported_by"), notNullValue());
        assertThat(first.get("reviewed_by"), notNullValue());

        Map<String, Object> second = output.getRows().get(1);
        assertThat(second.get("id"), is("b3f1c9d2-4b7e-4f8a-9c3d-2e5b7f9a1c2e"));
        assertThat(second.get("status"), is("pending"));

        assertThat(MockDeelController.headers.get("authorization"), is("Bearer test-token"));
        assertThat(MockDeelController.headers.get("x-version"), is("2026-01-01"));
    }

    @Test
    void testListTimesheetsWithFilters() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "ts-1",
                        "type": "work",
                        "status": "approved",
                        "contract": {"id": "contract_abc123", "type": "ongoing_time_based", "title": "Contract"},
                        "quantity": 8,
                        "created_at": "2024-06-15T09:30:00Z",
                        "description": "Work",
                        "reported_by": {"id": "user-1"},
                        "total_amount": "200.0000",
                        "currency_code": "USD",
                        "date_submitted": "2024-06-22T10:00:00Z"
                    }
                ],
                "page": {"total_rows": 1}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .contractId(Property.ofValue("contract_abc123"))
            .status(Property.ofValue("approved"))
            .dateFrom(Property.ofValue("2024-01-01"))
            .dateTo(Property.ofValue("2024-12-31"))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(1));
        assertThat(output.getRows().get(0).get("status"), is("approved"));

        assertThat(MockDeelController.queryParameters.get("contract_id"), is("contract_abc123"));
        assertThat(MockDeelController.queryParameters.get("statuses"), is("approved"));
        assertThat(MockDeelController.queryParameters.get("date_from"), is("2024-01-01"));
        assertThat(MockDeelController.queryParameters.get("date_to"), is("2024-12-31"));
    }

    @Test
    void testListTimesheetsPagination() throws Exception {
        String response = """
            {
                "data": [],
                "page": {"total_rows": 50}
            }
            """;

        MockDeelController.stubResponse(response);

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = List.builder()
            .apiToken(Property.ofValue("test-token"))
            .baseUrl(Property.ofValue("http://localhost:" + embeddedServer.getPort() + "/mock"))
            .limit(Property.ofValue(20))
            .offset(Property.ofValue(20))
            .fetchType(Property.ofValue(FetchType.FETCH))
            .build();

        List.Output output = task.run(runContext);

        assertThat(output.getSize(), is(0));
        assertThat(output.getTotal(), is(50L));
        assertThat(MockDeelController.queryParameters.get("limit"), is("20"));
        assertThat(MockDeelController.queryParameters.get("offset"), is("20"));
    }

    @Test
    void testListTimesheetsEmptyResult() throws Exception {
        String response = """
            {
                "data": [],
                "page": {"total_rows": 0}
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
    void testListTimesheetsFetchOne() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "1",
                        "type": "work",
                        "status": "approved",
                        "contract": {"id": "c1", "type": "ongoing_time_based", "title": "Contract"},
                        "quantity": 8,
                        "created_at": "2024-06-15T09:30:00Z",
                        "description": "Work",
                        "reported_by": {"id": "user-1"},
                        "total_amount": "200.0000",
                        "currency_code": "USD",
                        "date_submitted": "2024-06-22T10:00:00Z"
                    },
                    {
                        "id": "2",
                        "type": "work",
                        "status": "pending",
                        "contract": {"id": "c1", "type": "ongoing_time_based", "title": "Contract"},
                        "quantity": 4,
                        "created_at": "2024-06-16T09:30:00Z",
                        "description": "More work",
                        "reported_by": {"id": "user-1"},
                        "total_amount": "100.0000",
                        "currency_code": "USD",
                        "date_submitted": "2024-06-23T10:00:00Z"
                    }
                ],
                "page": {"total_rows": 2}
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
    void testListTimesheetsFetchNone() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "1",
                        "type": "work",
                        "status": "approved",
                        "contract": {"id": "c1", "type": "ongoing_time_based", "title": "Contract"},
                        "quantity": 8,
                        "created_at": "2024-06-15T09:30:00Z",
                        "description": "Work",
                        "reported_by": {"id": "user-1"},
                        "total_amount": "200.0000",
                        "currency_code": "USD",
                        "date_submitted": "2024-06-22T10:00:00Z"
                    }
                ],
                "page": {"total_rows": 1}
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
    void testListTimesheetsStore() throws Exception {
        String response = """
            {
                "data": [
                    {
                        "id": "1",
                        "type": "work",
                        "status": "approved",
                        "contract": {"id": "c1", "type": "ongoing_time_based", "title": "Contract"},
                        "quantity": 8,
                        "created_at": "2024-06-15T09:30:00Z",
                        "description": "Work",
                        "reported_by": {"id": "user-1"},
                        "total_amount": "200.0000",
                        "currency_code": "USD",
                        "date_submitted": "2024-06-22T10:00:00Z"
                    }
                ],
                "page": {"total_rows": 1}
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
    void testListTimesheetsUnauthorized() {
        MockDeelController.stubError(401, "Unauthorized");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("401"));
    }

    @Test
    void testListTimesheetsForbidden() {
        MockDeelController.stubError(403, "Forbidden");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("403"));
    }

    @Test
    void testListTimesheetsRateLimited() {
        MockDeelController.stubError(429, "Too Many Requests");

        RunContextFactory factory = applicationContext.getBean(RunContextFactory.class);
        RunContext runContext = factory.of();

        List task = buildTask(FetchType.FETCH);

        Exception e = assertThrows(Exception.class, () -> task.run(runContext));
        assertThat(e.getMessage(), containsString("429"));
    }
}
