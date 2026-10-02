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
@Schema(title = "Deel Time Off Daily")
public class DeelTimeOffDaily {

    @Schema(description = "Time off daily id")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Time off daily date")
    @JsonProperty("date")
    private String date;

    @Schema(description = "Time off daily type (WORKING_DAY, NON_WORKING_DAY, HOLIDAY, FORFAIT_HOUR_DAY)")
    @JsonProperty("type")
    private String type;

    @Schema(description = "Time off daily amount")
    @JsonProperty("amount")
    private Double amount;

    @Schema(description = "Time off daily creation date")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Time off daily update date")
    @JsonProperty("updated_at")
    private String updatedAt;

    @Schema(description = "Time off id")
    @JsonProperty("time_off_id")
    private String timeOffId;

    @Schema(description = "Time off daily description")
    @JsonProperty("description")
    private String description;
}
