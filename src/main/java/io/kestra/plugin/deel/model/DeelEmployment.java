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
@Schema(title = "Deel Employment")
public class DeelEmployment {

    @Schema(description = "Unique identifier of the employment")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Contract ID associated with this employment")
    @JsonProperty("contract_id")
    private String contractId;

    @Schema(description = "Contract type")
    @JsonProperty("contract_type")
    private String contractType;

    @Schema(description = "Contract title")
    @JsonProperty("contract_title")
    private String contractTitle;

    @Schema(description = "Employment status")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Job title")
    @JsonProperty("job_title")
    private String jobTitle;

    @Schema(description = "Department")
    @JsonProperty("department")
    private String department;

    @Schema(description = "Legal entity ID")
    @JsonProperty("legal_entity_id")
    private String legalEntityId;

    @Schema(description = "Legal entity name")
    @JsonProperty("legal_entity_name")
    private String legalEntityName;

    @Schema(description = "Team ID")
    @JsonProperty("team_id")
    private String teamId;

    @Schema(description = "Team name")
    @JsonProperty("team_name")
    private String teamName;

    @Schema(description = "Manager ID")
    @JsonProperty("manager_id")
    private String managerId;

    @Schema(description = "Manager name")
    @JsonProperty("manager_name")
    private String managerName;

    @Schema(description = "Start date")
    @JsonProperty("start_date")
    private String startDate;

    @Schema(description = "End date")
    @JsonProperty("end_date")
    private String endDate;

    @Schema(description = "Seniority")
    @JsonProperty("seniority")
    private String seniority;

    @Schema(description = "Employment type")
    @JsonProperty("employment_type")
    private String employmentType;

    @Schema(description = "Salary information")
    @JsonProperty("salary")
    private DeelSalary salary;

    @Schema(description = "Equity information")
    @JsonProperty("equity")
    private DeelEquity equity;

    @Schema(description = "Time off policy")
    @JsonProperty("time_off_policy")
    private String timeOffPolicy;

    @Schema(description = "Creation timestamp")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Last update timestamp")
    @JsonProperty("updated_at")
    private String updatedAt;
}