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
@Schema(title = "Deel Time Off profile response")
public class DeelTimeOffPage {

    @Schema(description = "Time off requests for the profile")
    @JsonProperty("data")
    private List<DeelTimeOff> data;

    @Schema(description = "Page size")
    @JsonProperty("page_size")
    private Integer pageSize;

    @Schema(description = "Whether another page is available")
    @JsonProperty("has_next_page")
    private Boolean hasNextPage;

    @Schema(description = "Cursor for the next page")
    @JsonProperty("next")
    private String next;

    @Schema(description = "Total count of items")
    @JsonProperty("count")
    private Integer count;
}
