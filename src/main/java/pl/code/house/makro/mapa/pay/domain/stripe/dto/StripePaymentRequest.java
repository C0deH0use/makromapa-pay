package pl.code.house.makro.mapa.pay.domain.stripe.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Currency;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class StripePaymentRequest {

  @NotNull Currency currency;
  @NotNull long amount;

  @JsonCreator
  public StripePaymentRequest(@JsonProperty("currency") Currency currency, @JsonProperty("amount") long amount) {
    this.currency = currency;
    this.amount = amount;
  }
}
