package pl.code.house.makro.mapa.pay.domain.stripe.dto;

import com.stripe.model.PaymentIntent;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentIntentDto {

  Long amount;
  Long capturedAmount;
  Long receivedAmount;
  String clientSecret;
  String currency;
  String description;
  Map<String, String> metadata;
  String status;

  public static PaymentIntentDto fromIntent(PaymentIntent paymentIntent) {
    return PaymentIntentDto.builder()
        .amount(paymentIntent.getAmount())
        .currency(paymentIntent.getCurrency())
        .capturedAmount(paymentIntent.getAmountCapturable())
        .receivedAmount(paymentIntent.getAmountReceived())
        .description(paymentIntent.getDescription())
        .metadata(paymentIntent.getMetadata())
        .status(paymentIntent.getStatus())
        .clientSecret(paymentIntent.getClientSecret())
        .build();
  }
}
