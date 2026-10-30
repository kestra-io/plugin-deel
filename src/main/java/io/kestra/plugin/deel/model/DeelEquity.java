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
@Schema(title = "Deel Equity")
public class DeelEquity {

    @Schema(description = "Equity grant ID")
    @JsonProperty("grant_id")
    private String grantId;

    @Schema(description = "Number of shares")
    @JsonProperty("shares")
    private String shares;

    @Schema(description = "Equity type")
    @JsonProperty("type")
    private String type;

    @Schema(description = "Vesting schedule")
    @JsonProperty("vesting_schedule")
    private String vestingSchedule;
}