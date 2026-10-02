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
@Schema(title = "Deel Platform Fee Invoice")
public class DeelDeelInvoice {

    @Schema(description = "Unique identifier of the invoice")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Label of the invoice")
    @JsonProperty("label")
    private String label;

    @Schema(description = "Total invoice amount including fee and VAT")
    @JsonProperty("total")
    private String total;

    @Schema(description = "Current status of the invoice (pending, paid, processing, canceled, skipped, failed, refunded)")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Currency code")
    @JsonProperty("currency")
    private String currency;

    @Schema(description = "Creation timestamp (ISO-8601)")
    @JsonProperty("created_at")
    private String createdAt;
}
