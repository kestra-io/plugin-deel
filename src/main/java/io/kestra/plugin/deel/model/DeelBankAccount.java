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
@Schema(title = "Deel Bank Account")
public class DeelBankAccount {

    @Schema(description = "Account holder name")
    @JsonProperty("account_holder_name")
    private String accountHolderName;

    @Schema(description = "Bank name")
    @JsonProperty("bank_name")
    private String bankName;

    @Schema(description = "Account number (masked)")
    @JsonProperty("account_number")
    private String accountNumber;

    @Schema(description = "Routing number")
    @JsonProperty("routing_number")
    private String routingNumber;

    @Schema(description = "IBAN")
    @JsonProperty("iban")
    private String iban;

    @Schema(description = "SWIFT/BIC code")
    @JsonProperty("swift_bic")
    private String swiftBic;

    @Schema(description = "Currency")
    @JsonProperty("currency")
    private String currency;

    @Schema(description = "Account type")
    @JsonProperty("account_type")
    private String accountType;
}