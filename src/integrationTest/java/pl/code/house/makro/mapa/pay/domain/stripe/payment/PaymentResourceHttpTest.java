package pl.code.house.makro.mapa.pay.domain.stripe.payment;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.Clock;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.context.WebApplicationContext;
import pl.code.house.makro.mapa.pay.MockOAuth2User;

@MockOAuth2User
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class PaymentResourceHttpTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private Clock clock;

  @BeforeEach
  void setUp() {
    webAppContextSetup(context, springSecurity());
  }

  @Test
  @DisplayName("should create new Stripe Payment Intend for current user")
  void shouldCreateNewStripePaymentIntendForCurrentUser() {
    //given
    RestAssuredMockMvc.given()
        .header(getAuthenticationHeader())
        .contentType(JSON)
        .body(paymentRequest())

        .when()
        .put("/api/v1/stripe/payment")

        .then()
        .log().ifValidationFails()
        .status(CREATED)

        .body("amount", equalTo(800))
        .body("capturedAmount", equalTo(0))
        .body("receivedAmount", equalTo(0))
        .body("currency", equalTo("pln"))
        .body("description", nullValue())
        .body("clientSecret", is(not(blankOrNullString())))
        .body("status", is(not(blankOrNullString())))
    ;
  }

  private Map<String, Object> paymentRequest() {
    return Map.of(
        "productId", "prod_JLJ8zD5OU23DjR",
        "amount", 800L,
        "currency", "PLN"
    );
  }
}