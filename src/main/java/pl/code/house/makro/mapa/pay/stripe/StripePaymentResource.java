package pl.code.house.makro.mapa.pay.stripe;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.code.house.makro.mapa.pay.stripe.dto.PaymentIntentDto;
import pl.code.house.makro.mapa.pay.stripe.dto.StripePaymentRequest;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/stripe", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
class StripePaymentResource {

  private final StripePaymentService paymentService;

  @ResponseStatus(CREATED)
  @PutMapping(value = "/payment")
  PaymentIntentDto createIntentPayment(
      @AuthenticationPrincipal String principal,
      @Valid @NotNull StripePaymentRequest request) {
    log.info("Request to create new Stripe IntentPayment for user `{}`", principal);

    return paymentService.createNewPaymentIntent(request);
  }

}
