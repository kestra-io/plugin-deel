package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel HRX Documents page")
public class DeelHrxDocumentPage {

    @Schema(description = "List of employee documents")
    @JsonProperty("data")
    private List<DeelHrxDocument> data;

    @Schema(description = "Indicates if there are more pages available")
    @JsonProperty("has_more")
    private Boolean hasMore;

    @Schema(description = "Cursor to use for the next page. Null if there are no more pages.")
    @JsonProperty("next_cursor")
    private String nextCursor;

    @Schema(description = "Total number of documents available across all pages")
    @JsonProperty("total_count")
    private Integer totalCount;
}
