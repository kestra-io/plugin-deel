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
@Schema(title = "Deel Timesheet Preset")
public class DeelTimesheetPreset {

    @Schema(description = "Unique identifier of the related timesheet preset")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Hourly rate of the related timesheet preset")
    @JsonProperty("rate")
    private Double rate;

    @Schema(description = "Title of the related timesheet preset")
    @JsonProperty("title")
    private String title;

    @Schema(description = "Description of the related timesheet preset")
    @JsonProperty("description")
    private String description;
}
