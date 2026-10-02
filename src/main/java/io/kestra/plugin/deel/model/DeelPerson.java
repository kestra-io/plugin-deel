package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel Person")
public class DeelPerson {

    @Schema(description = "Unique identifier of the person")
    @JsonProperty("id")
    private String id;

    @Schema(description = "First name of the person")
    @JsonProperty("first_name")
    private String firstName;

    @Schema(description = "Last name of the person")
    @JsonProperty("last_name")
    private String lastName;

    @Schema(description = "Full name of the person")
    @JsonProperty("full_name")
    private String fullName;

    @Schema(description = "Email address of the person")
    @JsonProperty("email")
    private String email;

    @Schema(description = "Personal email address")
    @JsonProperty("personal_email")
    private String personalEmail;

    @Schema(description = "Phone number")
    @JsonProperty("phone")
    private String phone;

    @Schema(description = "Timezone of the person")
    @JsonProperty("timezone")
    private String timezone;

    @Schema(description = "Locale")
    @JsonProperty("locale")
    private String locale;

    @Schema(description = "Date of birth")
    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    @Schema(description = "Gender")
    @JsonProperty("gender")
    private String gender;

    @Schema(description = "Nationality")
    @JsonProperty("nationality")
    private String nationality;

    @Schema(description = "Hiring status")
    @JsonProperty("hiring_status")
    private String hiringStatus;

    @Schema(description = "Hiring type")
    @JsonProperty("hiring_type")
    private String hiringType;

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

    @Schema(description = "Employment details")
    @JsonProperty("employments")
    private java.util.List<DeelEmployment> employments;

    @Schema(description = "Address information")
    @JsonProperty("address")
    private DeelAddress address;

    @Schema(description = "Bank account details")
    @JsonProperty("bank_account")
    private DeelBankAccount bankAccount;

    @Schema(description = "Creation timestamp")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Last update timestamp")
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * Shared mapping used by both the PeopleList task and the PersonTrigger,
     * so trigger outputs match task outputs.
     */
    public static Map<String, Object> toMap(DeelPerson person) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", person.getId());
        map.put("first_name", person.getFirstName());
        map.put("last_name", person.getLastName());
        map.put("full_name", person.getFullName());
        map.put("email", person.getEmail());
        map.put("personal_email", person.getPersonalEmail());
        map.put("phone", person.getPhone());
        map.put("timezone", person.getTimezone());
        map.put("locale", person.getLocale());
        map.put("date_of_birth", person.getDateOfBirth());
        map.put("gender", person.getGender());
        map.put("nationality", person.getNationality());
        map.put("hiring_status", person.getHiringStatus());
        map.put("hiring_type", person.getHiringType());
        map.put("job_title", person.getJobTitle());
        map.put("department", person.getDepartment());
        map.put("legal_entity_id", person.getLegalEntityId());
        map.put("legal_entity_name", person.getLegalEntityName());
        map.put("team_id", person.getTeamId());
        map.put("team_name", person.getTeamName());
        map.put("manager_id", person.getManagerId());
        map.put("manager_name", person.getManagerName());
        map.put("start_date", person.getStartDate());
        map.put("end_date", person.getEndDate());
        map.put("employments", person.getEmployments());
        map.put("address", person.getAddress());
        map.put("bank_account", person.getBankAccount());
        map.put("created_at", person.getCreatedAt());
        map.put("updated_at", person.getUpdatedAt());
        return map;
    }
}