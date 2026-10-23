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
@Schema(title = "Deel Payment Statement Beneficiary Details")
public class DeelPaymentStatementBeneficiary {

    @Schema(description = "IBAN of the beneficiary account")
    @JsonProperty("iban")
    private String iban;

    @Schema(description = "SWIFT/BIC code of the beneficiary bank")
    @JsonProperty("swift")
    private String swift;

    @Schema(description = "Two-letter country code of the beneficiary")
    @JsonProperty("country")
    private String country;

    @Schema(description = "Three-letter currency code of the beneficiary account (ISO 4217)")
    @JsonProperty("currency")
    private String currency;

    @Schema(description = "Name of the beneficiary bank")
    @JsonProperty("bank_name")
    private String bankName;

    @Schema(description = "Sort code of the beneficiary bank")
    @JsonProperty("sort_code")
    private String sortCode;

    @Schema(description = "Name of the beneficiary account holder")
    @JsonProperty("account_holder")
    private String accountHolder;

    @Schema(description = "Beneficiary account number")
    @JsonProperty("account_number")
    private String accountNumber;
}
