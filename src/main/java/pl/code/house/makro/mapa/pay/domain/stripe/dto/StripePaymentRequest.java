package pl.code.house.makro.mapa.pay.domain.stripe.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Currency;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class StripePaymentRequest {

  String paymentMethod;

  @NotNull
  Currency currency;

  @NotBlank
  String productId;

  @NotNull
  @Min(value = 200, message = "Min value is 200, being 2 zl PLN")
  long amount;

  @JsonCreator
  public StripePaymentRequest(
      @JsonProperty("paymentMethod") String paymentMethod,
      @JsonProperty("currency") Currency currency,
      @JsonProperty("productId") String productId,
      @JsonProperty("amount") long amount
  ) {
    this.paymentMethod = paymentMethod;
    this.currency = currency;
    this.productId = productId;
    this.amount = amount;
  }
}
