package pl.code.house.makro.mapa.pay.domain.stripe;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.stripe.param.PaymentMethodListParams.Type;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

    return paymentService.createNewPaymentIntent(request, customerId);
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
