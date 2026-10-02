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
@Schema(title = "Deel HRX Document download")
public class DeelHrxDownload {

    @Schema(description = "Pre-signed URL to download the HRX document PDF. Valid for 15 minutes.")
    @JsonProperty("url")
    private String url;

    @Schema(description = "When the document was created")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "When the document was last updated")
    @JsonProperty("updated_at")
    private String updatedAt;
}
