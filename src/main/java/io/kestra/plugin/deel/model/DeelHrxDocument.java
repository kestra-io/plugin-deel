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
@Schema(title = "Deel HRX Document")
public class DeelHrxDocument {

    @Schema(description = "Unique identifier for the employee document")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Name of the employee document")
    @JsonProperty("name")
    private String name;

    @Schema(description = "Type of the document category")
    @JsonProperty("category")
    private String category;

    @Schema(description = "When the document was created")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "When the document was last updated")
    @JsonProperty("updated_at")
    private String updatedAt;

    @Schema(description = "Unique identifier for the document category")
    @JsonProperty("category_id")
    private String categoryId;

    @Schema(description = "Type of the document category")
    @JsonProperty("category_type")
    private String categoryType;

    @Schema(description = "Description of the document category")
    @JsonProperty("category_description")
    private String categoryDescription;
}
