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
@Schema(title = "Deel Timesheet Review")
public class DeelTimesheetReview {

    @Schema(description = "Unique identifier of the reviewer")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Reviewer remarks or decision notes")
    @JsonProperty("remarks")
    private String remarks;

    @Schema(description = "Timestamp when the review occurred (ISO-8601)")
    @JsonProperty("reviewed_at")
    private String reviewedAt;

    @Schema(description = "Full name of the reviewer")
    @JsonProperty("full_name")
    private String fullName;
}
