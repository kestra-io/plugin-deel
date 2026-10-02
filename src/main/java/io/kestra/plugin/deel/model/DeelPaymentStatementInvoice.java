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
@Schema(title = "Deel Payment Statement Invoice")
public class DeelPaymentStatementInvoice {

    @Schema(description = "Unique identifier of the invoice")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Amount of the invoice")
    @JsonProperty("amount")
    private String amount;

    @Schema(description = "Three-letter currency code of the invoice (ISO 4217)")
    @JsonProperty("currency")
    private String currency;

    @Schema(description = "Descriptive label for the invoice")
    @JsonProperty("label")
    private String label;

    @Schema(description = "True when Deel automatically attached this invoice beyond the requested set")
    @JsonProperty("is_auto_added")
    private Boolean autoAdded;
}
