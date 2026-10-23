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
@Schema(title = "Deel Payment Statement")
public class DeelPaymentStatement {

    @Schema(description = "Unique identifier of the payment statement")
    @JsonProperty("id")
    private String id;

    @Schema(description = "Total amount of the payment statement before any balance is applied")
    @JsonProperty("amount")
    private String amount;

    @Schema(description = "Normalized status of the payment statement (PROCESSING, PAID, CANCELLED, FAILED)")
    @JsonProperty("status")
    private String status;

    @Schema(description = "Authoritative list of invoices settled by this payment statement")
    @JsonProperty("invoices")
    private List<DeelPaymentStatementInvoice> invoices;

    @Schema(description = "Amount still due after any applied balance and fee credits")
    @JsonProperty("amount_due")
    private String amountDue;

    @Schema(description = "Three-letter currency code of the payment statement (ISO 4217)")
    @JsonProperty("payment_currency")
    private String paymentCurrency;

    @Schema(description = "Date and time when the client paid (ISO-8601)")
    @JsonProperty("paid_at")
    private String paidAt;

    @Schema(description = "Reference used when reconciling this payment statement against incoming wires")
    @JsonProperty("reference")
    private String reference;

    @Schema(description = "Date and time when the payment statement was created (ISO-8601)")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "Method used to settle the payment statement")
    @JsonProperty("payment_method")
    private String paymentMethod;

    @Schema(description = "Amount of client balance applied to the payment statement")
    @JsonProperty("balance_applied")
    private String balanceApplied;

    @Schema(description = "Beneficiary bank details the payment is settled to")
    @JsonProperty("beneficiary_details")
    private DeelPaymentStatementBeneficiary beneficiaryDetails;

    @Schema(description = "Amount of fee credits applied to the payment statement")
    @JsonProperty("fee_credits_applied")
    private String feeCreditsApplied;
}
