package pl.code.house.makro.mapa.pay.domain.stripe;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import io.vavr.control.Try;
import javax.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/stripe/webhook", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
class WebHookResource {

  private static final String STRIPE_SIGNATURE_HEADER = "HTTP_STRIPE_SIGNATURE";
  private static final String PAYMENT_INTENT_SUCCEEDED_EVENT_TYPE = "payment_intent.succeeded";

  @PermitAll
  @PostMapping
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

}
