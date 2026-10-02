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
@Schema(title = "Deel Seniority Level")
public class DeelSeniority {

    @Schema(description = "A unique identifier for the seniority level")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "The name of the seniority level")
    @JsonProperty("name")
    private String name;

    @Schema(description = "The hierarchical level of seniority, where higher numbers indicate greater seniority")
    @JsonProperty("level")
    private Double level;
}
