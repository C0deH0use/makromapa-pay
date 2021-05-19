package pl.code.house.makro.mapa.pay.domain.stripe;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PaymentIntentDto;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.StripePaymentRequest;
import pl.code.house.makro.mapa.pay.error.CreatePaymentIntentException;

@Service
public class PaymentService extends StripeBaseApi {

  PaymentService(@Value("${payments.stripe.api-key}") String stripeApiKey) {
    super(stripeApiKey);
  }

  PaymentIntentDto createNewPaymentIntent(StripePaymentRequest request) {
    PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
        .setCurrency("PLN")
        .setPaymentMethod(request.getPaymentMethod())
        .setAmount(request.getAmount())
        .build();

    return Try.of(() -> PaymentIntent.create(createParams))
        .map(PaymentIntentDto::fromIntent)
        .getOrElseThrow(CreatePaymentIntentException::new);
  }
}
