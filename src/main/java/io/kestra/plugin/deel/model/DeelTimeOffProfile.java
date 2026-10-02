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
@Schema(title = "Deel Time Off Profile Reference")
public class DeelTimeOffProfile {

    @Schema(description = "Recipient/requester HRIS profile id")
    @JsonProperty("hris_profile_id")
    private String hrisProfileId;

    @Schema(description = "Recipient/requester organization id")
    @JsonProperty("organization_id")
    private String organizationId;

    @Schema(description = "Recipient/requester client profile id")
    @JsonProperty("client_profile_id")
    private String clientProfileId;
}
