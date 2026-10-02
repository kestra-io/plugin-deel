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
public class DeelPage<T> {

    @Schema(description = "List of items in the current page")
    @JsonProperty("data")
    private List<T> data;

    @Schema(description = "Pagination metadata")
    @JsonProperty("page")
    private DeelPagination page;

    public DeelPage(List<T> data, DeelPagination page) {
        this.data = data;
        this.page = page;
    }
}