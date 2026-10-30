package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel API pagination metadata")
public class DeelPagination {

    @Schema(description = "Offset of the first item in the current page (offset-based pagination)")
    @JsonProperty("offset")
    private Long offset;

    @Schema(description = "Total number of items across all pages")
    @JsonProperty("total_rows")
    private Long totalRows;

    @Schema(description = "Number of items per page")
    @JsonProperty("items_per_page")
    private Long itemsPerPage;

    @Schema(description = "Cursor for the next page (cursor-based pagination)")
    @JsonProperty("cursor")
    private String cursor;

    public DeelPagination(Long offset, Long totalRows, Long itemsPerPage, String cursor) {
        this.offset = offset;
        this.totalRows = totalRows;
        this.itemsPerPage = itemsPerPage;
        this.cursor = cursor;
    }
}