package pl.code.house.makro.mapa.pay.domain.stripe.payment;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.stripe.Stripe;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import pl.code.house.makro.mapa.pay.MockOAuth2User;

@MockOAuth2User
@SpringBootTest
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
    stubFor(post(urlEqualTo("/v1/payment_intents"))
        .withRequestBody(containing("amount=800&currency=PLN"))
        .withHeader(CONTENT_TYPE, WireMock.equalTo("application/x-www-form-urlencoded;charset=UTF-8"))
        .willReturn(aResponse()
            .withBodyFile("stripe/create_intent.json"))
    );

    given()
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
        .body("clientSecret", equalTo("pi_1Iu13DFkiRYX5yMrf9huakhP_secret_r0Acke6RiHGIAqJtOy0dUgDJ3"))
        .body("status", equalTo("requires_payment_method"))
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