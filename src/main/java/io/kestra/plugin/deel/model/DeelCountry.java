package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel Country")
public class DeelCountry {

    @Schema(description = "Country code")
    @JsonProperty("code")
    private String code;

    @Schema(description = "Country name")
    @JsonProperty("name")
    private String name;

    @Schema(description = "Sub-territories of the country")
    @JsonProperty("states")
    private List<DeelCountryState> states;

    @Schema(description = "State classification type")
    @JsonProperty("state_type")
    private String stateType;

    @Schema(description = "Employer of Record availability")
    @JsonProperty("eor_support")
    private Boolean eorSupport;

    @Schema(description = "Visa support status")
    @JsonProperty("visa_support")
    private Boolean visaSupport;

    @Schema(description = "Default currency")
    @JsonProperty("default_currency")
    private String defaultCurrency;
}
