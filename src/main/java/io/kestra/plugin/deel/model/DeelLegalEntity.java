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
@Schema(title = "Deel Legal Entity")
public class DeelLegalEntity {

    @Schema(description = "Id of the legal entity")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Name of the legal entity")
    @JsonProperty("name")
    private String name;

    @Schema(description = "Phone number")
    @JsonProperty("phone")
    private String phone;

    @Schema(description = "VAT ID")
    @JsonProperty("vat_id")
    private String vatId;

    @Schema(description = "Address of the legal entity")
    @JsonProperty("address")
    private DeelLegalEntityAddress address;

    @Schema(description = "Country of the legal entity")
    @JsonProperty("country")
    private String country;

    @Schema(description = "Created date")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "SIC Company Identifier")
    @JsonProperty("sic_number")
    private String sicNumber;

    @Schema(description = "Updated date")
    @JsonProperty("updated_at")
    private String updatedAt;

    @Schema(description = "Archived date")
    @JsonProperty("archived_at")
    private String archivedAt;

    @Schema(description = "Entity type (individual, company, light)")
    @JsonProperty("entity_type")
    private String entityType;

    @Schema(description = "Industry name")
    @JsonProperty("industry_name")
    private String industryName;

    @Schema(description = "Entity sub type")
    @JsonProperty("entity_subtype")
    private String entitySubtype;

    @Schema(description = "Registration number")
    @JsonProperty("registrationNumber")
    private String registrationNumber;
}
