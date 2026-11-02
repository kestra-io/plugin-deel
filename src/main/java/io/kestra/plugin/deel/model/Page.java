package io.kestra.plugin.deel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Deel API paginated response")
public class Page<T> {

    @Schema(description = "List of items in the current page")
    @JsonProperty("data")
    private List<T> data;

    @Schema(description = "Pagination metadata")
    @JsonProperty("page")
    private Pagination page;

    public Page(List<T> data, Pagination page) {
        this.data = data;
        this.page = page;
    }
}