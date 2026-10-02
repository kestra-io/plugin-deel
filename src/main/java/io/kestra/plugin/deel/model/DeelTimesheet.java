package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel Timesheet")
public class DeelTimesheet {

    @Schema(description = "Unique identifier of the timesheet")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Timesheet type (always work)")
    @JsonProperty("type")
    private String type;

    @Schema(description = "Current processing status of the timesheet")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Contract associated with this timesheet")
    @JsonProperty("contract")
    private DeelTimesheetContract contract;

    @Schema(description = "Quantity of work units used to calculate the total amount")
    @JsonProperty("quantity")
    private Double quantity;

    @Schema(description = "Breakdown of time worked across different units")
    @JsonProperty("worksheet")
    private DeelTimesheetWorksheet worksheet;

    @Schema(description = "Timestamp when the timesheet was created (ISO-8601)")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Human-readable description of the work performed")
    @JsonProperty("description")
    private String description;

    @Schema(description = "User who submitted the timesheet")
    @JsonProperty("reported_by")
    private DeelTimesheetReporter reportedBy;

    @Schema(description = "Total monetary value of the timesheet")
    @JsonProperty("total_amount")
    private String totalAmount;

    @Schema(description = "ISO 4217 currency code used for the amount")
    @JsonProperty("currency_code")
    private String currencyCode;

    @Schema(description = "Timestamp when the timesheet was submitted for review (ISO-8601)")
    @JsonProperty("date_submitted")
    private String dateSubmitted;

    @Schema(description = "Predefined scale used to calculate the timesheet amount")
    @JsonProperty("scale")
    private String scale;

    @Schema(description = "File attachment linked to the timesheet")
    @JsonProperty("attachment")
    private DeelTimesheetAttachment attachment;

    @Schema(description = "Reviewer information if the timesheet has been reviewed")
    @JsonProperty("reviewed_by")
    private DeelTimesheetReview reviewedBy;

    @Schema(description = "Custom scale label defined by the client for non-standard billing rates")
    @JsonProperty("custom_scale")
    private String customScale;

    @Schema(description = "Payment cycle associated with this timesheet")
    @JsonProperty("payment_cycle")
    private DeelTimesheetPaymentCycle paymentCycle;

    @Schema(description = "Timesheet preset linked to this entry, if any")
    @JsonProperty("hourly_report_preset")
    private DeelTimesheetPreset hourlyReportPreset;
}
