package pl.code.house.makro.mapa.pay.domain.stripe;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentMethodListParams.Type;
import io.vavr.control.Try;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.CustomerDto;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PaymentIntentDto;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PaymentMethodDto;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.StripePaymentRequest;
import pl.code.house.makro.mapa.pay.domain.user.UserInfoDto;
import pl.code.house.makro.mapa.pay.domain.user.UserInfoFacade;
import pl.code.house.makro.mapa.pay.error.CustomerNotFoundException;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/stripe", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
class PaymentResource {

  private static final String STRIPE_SIGNATURE_HEADER = "HTTP_STRIPE_SIGNATURE";
  private static final String PAYMENT_INTENT_SUCCEEDED_EVENT_TYPE = "payment_intent.succeeded";
  private final UserInfoFacade userInfoFacade;
  private final PaymentService paymentService;
  private final CustomerService customerService;

  @ResponseStatus(CREATED)
  @PutMapping("/payment/intent")
  PaymentIntentDto createIntentPayment(
      @RequestHeader(AUTHORIZATION) String authHeaderValue,
      @RequestBody @Valid StripePaymentRequest request) {

    UserInfoDto userInfo = userInfoFacade.userInfo(authHeaderValue);
    String customerId = customerService.findCustomerByEmail(userInfo.email())
        .map(CustomerDto::id)
        .orElseGet(() -> customerService.storeNewCustomer(userInfo, request.getPaymentMethod()).id());

    log.info("Request to create new Stripe IntentPayment for user `{}` (Stripe Customer: {})",
        userInfo.name(), customerId);

    return paymentService.createNewPaymentIntent(request, customerId, userInfo.email());
  }

  @PermitAll
  @PostMapping("/payment/intent.succeeded")
  void paymentIntentSucceeded(
      @RequestBody String payload,
      @RequestHeader(STRIPE_SIGNATURE_HEADER) String webhookSignature,
      @Value("payments.stripe.endpoint-secret") String stripeApiKey) {

    PaymentIntent paymentIntent = Try.of(() -> Webhook.constructEvent(payload, webhookSignature, stripeApiKey))
        .onFailure(SignatureVerificationException.class, e -> log.error("Invalid Signature in WebHook request", e))
        .peek(event -> log.debug("Stripe WebHook event registered: {}, event data: {}", event.getType(), event.getData()))
        .filter(event -> PAYMENT_INTENT_SUCCEEDED_EVENT_TYPE.equals(event.getType()))
        .map(Event::getDataObjectDeserializer)
        .map(EventDataObjectDeserializer::getObject)
        .get()
        .map(obj -> (PaymentIntent) obj)
        .orElseThrow();

    log.debug("Payment Intent - {}, for customer: {} {}", paymentIntent.getId(), paymentIntent.getCustomer(), paymentIntent.getStatus());
  }

  @GetMapping("/payment/method")
  List<PaymentMethodDto> findPaymentMethods(
      @RequestHeader(AUTHORIZATION) String authHeaderValue) {

    UserInfoDto userInfo = userInfoFacade.userInfo(authHeaderValue);
    String customerId = customerService.findCustomerByEmail(userInfo.email())
        .map(CustomerDto::id)
        .orElseThrow(() -> new CustomerNotFoundException("No customer for current user `%s` found in Stripe.".formatted(userInfo.sub())));

    log.info("Request to create new Stripe IntentPayment for user `{}` (Stripe Customer: {})",
        userInfo.name(), customerId);

    return paymentService.findPaymentMethodsForCustomer(customerId, Type.CARD);
  }

}
