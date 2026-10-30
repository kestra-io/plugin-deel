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
@Schema(title = "Deel Cost Center")
public class DeelCostCenter {

    @Schema(description = "Cost center id")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Cost center date of registration on Deel")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Cost center date of most recent update on Deel")
    @JsonProperty("updated_at")
    private String updatedAt;

    @Schema(description = "Cost center name to be displayed")
    @JsonProperty("cost_center_name")
    private String costCenterName;

    @Schema(description = "Cost center code")
    @JsonProperty("cost_center_number")
    private String costCenterNumber;
}
