package pl.code.house.makro.mapa.pay.stripe;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.code.house.makro.mapa.pay.error.CreatePaymentIntentException;
import pl.code.house.makro.mapa.pay.stripe.dto.PaymentIntentDto;
import pl.code.house.makro.mapa.pay.stripe.dto.StripePaymentRequest;

@Service
class StripePaymentService {

  private final String stripeApiKey;

  StripePaymentService(@Value("${payments.stripe.api-key}") String stripeApiKey) {
    this.stripeApiKey = stripeApiKey;
  }

  PaymentIntentDto createNewPaymentIntent(StripePaymentRequest request) {
    Stripe.apiKey = stripeApiKey;
    PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
        .setCurrency("PLN")
        .setAmount(request.getAmount())
        .build();

    return Try.of(() -> PaymentIntent.create(createParams))
        .map(PaymentIntentDto::fromIntent)
        .getOrElseThrow(CreatePaymentIntentException::new);
  }
}
