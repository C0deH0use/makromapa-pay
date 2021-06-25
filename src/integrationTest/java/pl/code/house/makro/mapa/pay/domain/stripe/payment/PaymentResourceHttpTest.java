package pl.code.house.makro.mapa.pay.domain.stripe.payment;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAdminAuthenticationHeader;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import com.stripe.Stripe;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class PaymentResourceHttpTest {

  @Autowired
  private WebApplicationContext context;

  @Value("${wiremock.server.port}")
  private int wiremockPort;

  @BeforeEach
  void setUp() {
    webAppContextSetup(context, springSecurity());
    Stripe.overrideApiBase("http://localhost:%d".formatted(wiremockPort));
  }

  @Test
  @DisplayName("should create new Stripe Payment Intend for new customer")
  void shouldCreateNewStripePaymentIntendForNewCustomer() {
    //given
    given()
        .header(getAuthenticationHeader())
        .contentType("application/json; charset=utf-8")
        .body(payloadRequest("PLN", "pm_1J6KCQFkiRYX5yMrUSzqtFUK"))

        .when()
        .put("/api/v1/stripe/payment/intent")

        .then()
        .log().ifValidationFails()
        .status(CREATED)

        .body("paymentMethodId", equalTo("pm_1J6KCQFkiRYX5yMrUSzqtFUK"))
        .body("amount", equalTo(2000))
        .body("capturedAmount", equalTo(0))
        .body("receivedAmount", equalTo(0))
        .body("currency", equalTo("pln"))
        .body("description", nullValue())
        .body("clientSecret", equalTo("pi_1Iu13DFkiRYX5yMrf9huakhP_secret_r0Acke6RiHGIAqJtOy0dUgDJ3"))
        .body("status", equalTo("requires_confirmation"))
    ;
  }

  @Test
  @DisplayName("should create new PaymentIntent for existing admin customer")
  void shouldCreateNewPaymentIntentForExistingAdminCustomer() {
    given()
        .header(getAdminAuthenticationHeader())
        .contentType("application/json; charset=utf-8")
        .body(payloadRequest("PLN", "pm_1J5zmwFkiRYX5yMrL6QtpLVI"))

        .when()
        .put("/api/v1/stripe/payment/intent")

        .then()
        .log().ifValidationFails()
        .status(CREATED)

        .body("paymentMethodId", equalTo("pm_1J5zmwFkiRYX5yMrL6QtpLVI"))
        .body("amount", equalTo(2000))
        .body("capturedAmount", equalTo(0))
        .body("receivedAmount", equalTo(0))
        .body("currency", equalTo("pln"))
        .body("description", nullValue())
        .body("clientSecret", equalTo("pi_1Iu13DFkiRYX5yMrf9huakhP_secret_r0Acke6RiHGIAqJtOy0dUgDJ3"))
        .body("status", equalTo("success"))
    ;
  }

  @Test
  @DisplayName("should handle lower case currency values")
  void shouldHandleLowerCaseCurrencyValues() {
    given()
        .header(getAuthenticationHeader())
        .contentType("application/json; charset=utf-8")
        .body(payloadRequest("pln", "pm_1J6KCQFkiRYX5yMrUSzqtFUK"))

        .when()
        .put("/api/v1/stripe/payment/intent")

        .then()
        .log().ifValidationFails()
        .status(CREATED)

        .body("paymentMethodId", equalTo("pm_1J6KCQFkiRYX5yMrUSzqtFUK"))
        .body("amount", equalTo(2000))
        .body("capturedAmount", equalTo(0))
        .body("receivedAmount", equalTo(0))
        .body("currency", equalTo("pln"))
        .body("description", nullValue())
        .body("clientSecret", equalTo("pi_1Iu13DFkiRYX5yMrf9huakhP_secret_r0Acke6RiHGIAqJtOy0dUgDJ3"))
        .body("status", equalTo("requires_confirmation"))
    ;
  }

  @Test
  @DisplayName("should return all known payment methods for existing customer")
  void shouldReturnAllKnownPaymentMethodsForExistingCustomer() {
    //given
    given()
        .header(getAdminAuthenticationHeader())
        .contentType("application/json; charset=utf-8")

        .when()
        .get("/api/v1/stripe/payment/method")

        .then()
        .log().ifValidationFails()
        .status(OK)

        .body("$", hasSize(1))
        .body("[0].id", equalTo("pm_1J6HyzFkiRYX5yMrY7yC3ogq"))
        .body("[0].type", equalTo("card"))
        .body("[0].card.brand", equalTo("visa"))
        .body("[0].card.country", equalTo("US"))
        .body("[0].card.expMonth", equalTo(11))
        .body("[0].card.expYear", equalTo(2023))
        .body("[0].card.last4", equalTo("4242"))
        .body("[0].card.threeDSecureUsage.supported", equalTo(true))
    ;
  }

  @Test
  @DisplayName("should return PRECONDITION_REQUIRED when asking for payment method for user that is not a Stripe customer")
  void shouldReturnPreconditionRequiredWhenAskingForPaymentMethodForUserThatIsNotAStripeCustomer() {
    given()
        .header(getAuthenticationHeader())
        .contentType("application/json; charset=utf-8")

        .when()
        .get("/api/v1/stripe/payment/method")

        .then()
        .log().ifValidationFails()
        .status(PRECONDITION_REQUIRED)
        .body("uniqueErrorId", notNullValue(UUID.class))
        .body("error", equalTo("No customer for current user `b146b422-475c-4beb-9e9c-4e33e2288b08` found in Stripe."));

  }

  @NotNull
  private Map<String, ? extends Serializable> payloadRequest(String currency, String paymentMethod) {
    return Map.of(
        "paymentMethod", paymentMethod,
        "productId", "prod_JLJ8zD5OU23DjR",
        "amount", 2000,
        "currency", currency
    );
  }
}