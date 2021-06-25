package pl.code.house.makro.mapa.pay.domain.stripe;

import static java.util.stream.Collectors.toList;

import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;
import io.vavr.control.Try;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PaymentIntentDto;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PaymentMethodDto;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.StripePaymentRequest;
import pl.code.house.makro.mapa.pay.error.CreatePaymentIntentException;

@Service
public class PaymentService extends StripeBaseApi {

  PaymentService(@Value("${payments.stripe.api-key}") String stripeApiKey) {
    super(stripeApiKey);
  }

  PaymentIntentDto createNewPaymentIntent(StripePaymentRequest request, String customerId, String email) {
    PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
        .setCustomer(customerId)
        .setReceiptEmail(email)
        .setCurrency(request.getCurrency().getCurrencyCode())
        .setPaymentMethod(request.getPaymentMethod())
        .setAmount(request.getAmount())
        .build();

    return Try.of(() -> PaymentIntent.create(createParams))
        .map(PaymentIntentDto::fromIntent)
        .getOrElseThrow(CreatePaymentIntentException::new);
  }

  List<PaymentMethodDto> findPaymentMethodsForCustomer(String customerId, PaymentMethodListParams.Type paymentMethodType) {
    PaymentMethodListParams listParams = PaymentMethodListParams.builder()
        .setCustomer(customerId)
        .setType(paymentMethodType)
        .build();
    return Try.of(() -> PaymentMethod.list(listParams))
        .map(PaymentMethodCollection::getData)
        .toJavaStream()
        .flatMap(List::stream)
        .map(PaymentMethodDto::from)
        .collect(toList());
  }
}
