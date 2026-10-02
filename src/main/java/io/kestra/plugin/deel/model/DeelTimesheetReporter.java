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
@Schema(title = "Deel Timesheet Reporter")
public class DeelTimesheetReporter {

    @Schema(description = "Unique identifier of the reporting user")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Full name of the user who submitted the timesheet")
    @JsonProperty("full_name")
    private String fullName;
}
