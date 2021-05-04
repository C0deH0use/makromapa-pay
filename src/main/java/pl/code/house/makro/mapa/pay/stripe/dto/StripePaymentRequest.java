package pl.code.house.makro.mapa.pay.stripe.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class StripePaymentRequest {

  @NotNull long amount;

  @JsonCreator
  public StripePaymentRequest(@JsonProperty("amount") long amount) {
    this.amount = amount;
  }
}
