package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel Time Off Request")
public class DeelTimeOff {

    @Schema(description = "Time off id")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Amount of time off")
    @JsonProperty("amount")
    private Double amount;

    @Schema(description = "Is time off paid")
    @JsonProperty("is_paid")
    private Boolean paid;

    @Schema(description = "End date of time off")
    @JsonProperty("end_date")
    private String endDate;

    @Schema(description = "Time off creation date")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Start date of time off")
    @JsonProperty("start_date")
    private String startDate;

    @Schema(description = "Time off update date")
    @JsonProperty("updated_at")
    private String updatedAt;

    @Schema(description = "Time off request date")
    @JsonProperty("requested_at")
    private String requestedAt;

    @Schema(description = "Unit in which the time off usage and entitlement are calculated")
    @JsonProperty("entitlement_unit")
    private String entitlementUnit;

    @Schema(description = "Time off type id")
    @JsonProperty("time_off_type_id")
    private String timeOffTypeId;

    @Schema(description = "Reason for time off")
    @JsonProperty("reason")
    private String reason;

    @Schema(description = "Status of time off (REQUESTED, APPROVED, REJECTED, USED, CANCELED)")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Time off approval date")
    @JsonProperty("approved_at")
    private String approvedAt;

    @Schema(description = "Time off description")
    @JsonProperty("description")
    private String description;

    @Schema(description = "Contract id")
    @JsonProperty("contract_oid")
    private String contractOid;

    @Schema(description = "Deduction amount")
    @JsonProperty("deduction_amount")
    private Double deductionAmount;

    @Schema(description = "Daily breakdown of the time off request")
    @JsonProperty("time_off_dailies")
    private List<DeelTimeOffDaily> timeOffDailies;

    @Schema(description = "Recipient profile")
    @JsonProperty("recipient_profile")
    private DeelTimeOffProfile recipientProfile;

    @Schema(description = "Requester profile")
    @JsonProperty("requester_profile")
    private DeelTimeOffProfile requesterProfile;

    @Schema(description = "Time off percentage")
    @JsonProperty("time_off_percentage")
    private Double timeOffPercentage;

    @Schema(description = "Is end date estimated")
    @JsonProperty("is_end_date_estimated")
    private Boolean endDateEstimated;

    @Schema(description = "Other type description")
    @JsonProperty("other_type_description")
    private String otherTypeDescription;

    /**
     * Shared mapping used by both the timeoff List task and the TimeOffTrigger,
     * so trigger outputs match task outputs.
     */
    public static Map<String, Object> toMap(DeelTimeOff timeOff) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", timeOff.getId());
        map.put("amount", timeOff.getAmount());
        map.put("is_paid", timeOff.getPaid());
        map.put("start_date", timeOff.getStartDate());
        map.put("end_date", timeOff.getEndDate());
        map.put("status", timeOff.getStatus());
        map.put("reason", timeOff.getReason());
        map.put("description", timeOff.getDescription());
        map.put("entitlement_unit", timeOff.getEntitlementUnit());
        map.put("time_off_type_id", timeOff.getTimeOffTypeId());
        map.put("contract_oid", timeOff.getContractOid());
        map.put("deduction_amount", timeOff.getDeductionAmount());
        map.put("requested_at", timeOff.getRequestedAt());
        map.put("approved_at", timeOff.getApprovedAt());
        map.put("created_at", timeOff.getCreatedAt());
        map.put("updated_at", timeOff.getUpdatedAt());
        map.put("time_off_percentage", timeOff.getTimeOffPercentage());
        map.put("is_end_date_estimated", timeOff.getEndDateEstimated());
        map.put("other_type_description", timeOff.getOtherTypeDescription());
        map.put("time_off_dailies", timeOff.getTimeOffDailies());
        map.put("recipient_profile", timeOff.getRecipientProfile());
        map.put("requester_profile", timeOff.getRequesterProfile());
        return map;
    }
}
