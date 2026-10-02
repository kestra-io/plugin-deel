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
@Schema(title = "Deel Salary")
public class DeelSalary {

    @Schema(description = "Annual salary amount")
    @JsonProperty("annual_amount")
    private String annualAmount;

    @Schema(description = "Currency")
    @JsonProperty("currency")
    private String currency;

    @Schema(description = "Pay frequency")
    @JsonProperty("pay_frequency")
    private String payFrequency;

    @Schema(description = "Hourly rate")
    @JsonProperty("hourly_rate")
    private String hourlyRate;
}