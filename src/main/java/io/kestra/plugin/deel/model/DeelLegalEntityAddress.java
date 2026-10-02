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
@Schema(title = "Deel Legal Entity Address")
public class DeelLegalEntityAddress {

    @Schema(description = "Zip code")
    @JsonProperty("zip")
    private String zip;

    @Schema(description = "City")
    @JsonProperty("city")
    private String city;

    @Schema(description = "State")
    @JsonProperty("state")
    private String state;

    @Schema(description = "Street")
    @JsonProperty("street")
    private String street;

    @Schema(description = "Country")
    @JsonProperty("country")
    private String country;
}
