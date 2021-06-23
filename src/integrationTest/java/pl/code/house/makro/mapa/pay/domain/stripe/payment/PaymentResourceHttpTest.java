package pl.code.house.makro.mapa.pay.domain.stripe.payment;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import com.stripe.Stripe;
import java.io.Serializable;
import java.util.Map;
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
  @DisplayName("should create new Stripe Payment Intend for current user")
  void shouldCreateNewStripePaymentIntendForCurrentUser() {
    //given
    given()
        .header(getAuthenticationHeader())
        .contentType("application/json; charset=utf-8")
        .body(payloadRequest("PLN"))

        .when()
        .put("/api/v1/stripe/payment")

        .then()
        .log().ifValidationFails()
        .status(CREATED)

        .body("paymentMethodId", equalTo("pm_1J5HCaFkiRYX5yMr506R0vMZ"))
        .body("amount", equalTo(2000))
        .body("capturedAmount", equalTo(0))
        .body("receivedAmount", equalTo(0))
        .body("currency", equalTo("pln"))
        .body("description", nullValue())
        .body("clientSecret", equalTo("pi_1Iu13DFkiRYX5yMrf9huakhP_secret_r0Acke6RiHGIAqJtOy0dUgDJ3"))
        .body("status", equalTo("requires_payment_method"))
    ;
  }

  @Test
  @DisplayName("should handle lower case currency values")
  void shouldHandleLowerCaseCurrencyValues() {
    given()
        .header(getAuthenticationHeader())
        .contentType("application/json; charset=utf-8")
        .body(payloadRequest("pln"))

        .when()
        .put("/api/v1/stripe/payment")

        .then()
        .log().ifValidationFails()
        .status(CREATED)

        .body("paymentMethodId", equalTo("pm_1J5HCaFkiRYX5yMr506R0vMZ"))
        .body("amount", equalTo(2000))
        .body("capturedAmount", equalTo(0))
        .body("receivedAmount", equalTo(0))
        .body("currency", equalTo("pln"))
        .body("description", nullValue())
        .body("clientSecret", equalTo("pi_1Iu13DFkiRYX5yMrf9huakhP_secret_r0Acke6RiHGIAqJtOy0dUgDJ3"))
        .body("status", equalTo("requires_payment_method"))
    ;
  }

  @NotNull
  private Map<String, ? extends Serializable> payloadRequest(String currency) {
    return Map.of(
        "paymentMethod", "pm_1J5HCaFkiRYX5yMr506R0vMZ",
        "productId", "prod_JLJ8zD5OU23DjR",
        "amount", 2000,
        "currency", currency
    );
  }
}