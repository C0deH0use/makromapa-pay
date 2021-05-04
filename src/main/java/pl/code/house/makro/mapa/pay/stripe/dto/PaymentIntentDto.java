package pl.code.house.makro.mapa.pay.stripe.dto;

import com.stripe.model.Account;
import com.stripe.model.Application;
import com.stripe.model.ChargeCollection;
import com.stripe.model.Customer;
import com.stripe.model.ExpandableField;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentSource;
import com.stripe.model.Review;
import com.stripe.model.ShippingDetails;
import com.stripe.model.StripeError;
import java.util.List;
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
