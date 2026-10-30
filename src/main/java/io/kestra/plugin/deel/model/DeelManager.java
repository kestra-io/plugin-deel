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
@Schema(title = "Deel Manager")
public class DeelManager {

    @Schema(description = "Unique identifier of the manager")
    @JsonProperty("id")
    private String id;

    @Schema(description = "The email address of the manager")
    @JsonProperty("email")
    private String email;

    @Schema(description = "The last name of the manager")
    @JsonProperty("last_name")
    private String lastName;

    @Schema(description = "The first name of the manager")
    @JsonProperty("first_name")
    private String firstName;
}
