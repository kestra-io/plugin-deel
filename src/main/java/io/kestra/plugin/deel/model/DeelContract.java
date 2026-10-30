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
@Schema(title = "Deel Contract")
public class DeelContract {

    @Schema(description = "Unique identifier of the contract")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Title of the contract")
    @JsonProperty("title")
    private String title;

    @Schema(description = "Type of the contract (e.g., open, terminated, expired)")
    @JsonProperty("contract_type")
    private String contractType;

    @Schema(description = "Status of the contract (e.g., active, terminated)")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Legal entity ID")
    @JsonProperty("legal_entity_id")
    private String legalEntityId;

    @Schema(description = "Team ID")
    @JsonProperty("team_id")
    private String teamId;

    @Schema(description = "Start date of the contract (ISO 8601)")
    @JsonProperty("start_date")
    private String startDate;

    @Schema(description = "End date of the contract (ISO 8601)")
    @JsonProperty("end_date")
    private String endDate;

    @Schema(description = "Creation timestamp (ISO 8601)")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Last update timestamp (ISO 8601)")
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * Shared mapping used by both the ContractsList task and the ContractTrigger,
     * so trigger outputs match task outputs.
     */
    public static Map<String, Object> toMap(DeelContract contract) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", contract.getId());
        map.put("title", contract.getTitle());
        map.put("contract_type", contract.getContractType());
        map.put("status", contract.getStatus());
        map.put("legal_entity_id", contract.getLegalEntityId());
        map.put("team_id", contract.getTeamId());
        map.put("start_date", contract.getStartDate());
        map.put("end_date", contract.getEndDate());
        map.put("created_at", contract.getCreatedAt());
        map.put("updated_at", contract.getUpdatedAt());
        return map;
    }
}