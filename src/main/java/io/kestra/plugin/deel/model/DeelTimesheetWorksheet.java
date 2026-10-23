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
@Schema(title = "Deel Timesheet Worksheet")
public class DeelTimesheetWorksheet {

    @Schema(description = "Number of days reported")
    @JsonProperty("days")
    private Double days;

    @Schema(description = "Number of hours reported")
    @JsonProperty("hours")
    private Double hours;

    @Schema(description = "Number of weeks reported")
    @JsonProperty("weeks")
    private Double weeks;

    @Schema(description = "Number of minutes reported")
    @JsonProperty("minutes")
    private Double minutes;
}
