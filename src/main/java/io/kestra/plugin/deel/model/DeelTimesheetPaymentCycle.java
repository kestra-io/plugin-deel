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
@Schema(title = "Deel Timesheet Payment Cycle")
public class DeelTimesheetPaymentCycle {

    @Schema(description = "End date of the payment cycle (ISO-8601)")
    @JsonProperty("end_date")
    private String endDate;

    @Schema(description = "Start date of the payment cycle (ISO-8601)")
    @JsonProperty("start_date")
    private String startDate;
}
