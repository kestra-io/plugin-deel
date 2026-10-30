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
@Schema(title = "Deel Currency")
public class DeelCurrency {

    @Schema(description = "The ISO 4217 three-letter currency code")
    @JsonProperty("code")
    private String code;

    @Schema(description = "The name of the currency")
    @JsonProperty("name")
    private String name;
}
