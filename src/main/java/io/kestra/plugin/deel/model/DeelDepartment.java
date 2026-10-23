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
@Schema(title = "Deel Department")
public class DeelDepartment {

    @Schema(description = "The unique identifier for the department")
    @JsonProperty("id")
    private String id;

    @Schema(description = "The name of the department")
    @JsonProperty("name")
    private String name;

    @Schema(description = "The ID of the parent department, if applicable")
    @JsonProperty("parent")
    private String parent;
}
