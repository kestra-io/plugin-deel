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
@Schema(title = "Deel Timesheet Attachment")
public class DeelTimesheetAttachment {

    @Schema(description = "Attachment key used to upload the file")
    @JsonProperty("key")
    private String key;

    @Schema(description = "Original filename used for the upload")
    @JsonProperty("filename")
    private String filename;
}
