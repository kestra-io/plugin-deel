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
@Schema(title = "Deel Address")
public class DeelAddress {

    @Schema(description = "Street address line 1")
    @JsonProperty("street_address")
    private String streetAddress;

    @Schema(description = "Street address line 2")
    @JsonProperty("street_address_2")
    private String streetAddress2;

    @Schema(description = "City")
    @JsonProperty("city")
    private String city;

    @Schema(description = "State or province")
    @JsonProperty("state")
    private String state;

    @Schema(description = "Postal code")
    @JsonProperty("postal_code")
    private String postalCode;

    @Schema(description = "Country code (ISO 3166-1 alpha-2)")
    @JsonProperty("country")
    private String country;

    @Schema(description = "Country name")
    @JsonProperty("country_name")
    private String countryName;
}