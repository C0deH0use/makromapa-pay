package pl.code.house.makro.mapa.pay.stripe;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/stripe/payment", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
class StripePaymentResource {

  @PutMapping(value = "initialize")
  ResponseEntity<Object> initializeIntentPayment(@AuthenticationPrincipal(expression = "name") String principalName) {
    log.info("Request to initilize Stripe IntentPayment for user `{}`", principalName);

    return ok("");
  }

}
