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
@Schema(title = "Deel Timesheet Contract")
public class DeelTimesheetContract {

    @Schema(description = "Unique identifier of the contract")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Type of the contract")
    @JsonProperty("type")
    private String type;

    @Schema(description = "Human-readable title of the contract")
    @JsonProperty("title")
    private String title;
}
