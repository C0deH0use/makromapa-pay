package pl.code.house.makro.mapa.pay.stripe;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class StripePaymentResourceHttpTest {

  private static final String USER_SUB = "aa6641c1-e9f4-417f-adf4-f71accc470cb";
  private static final ObjectMapper mapper = new ObjectMapper();

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
    mockUserInfo();

    given()
        .header(getAuthenticationHeader())
        .contentType(JSON)
        .body(paymentRequest())

        .when()
        .put("/api/v1/stripe/payment")

        .then()
        .log().ifValidationFails()
        .status(CREATED)

        .body(blankOrNullString())
    ;
  }

  private Map<String, Object> paymentRequest() {
    return Map.of("amount", 1000L);
  }

  private static void mockUserInfo() {
    stubFor(post(urlEqualTo("/oauth/check_token"))
        .willReturn(okJson(tokenCheckResponse()))
    );
  }

  static String tokenCheckResponse() {
    Map<String, Object> tokenResponse = Map.of(
        "active", true,
        "user_name", USER_SUB,
        "aud", List.of("makromapa-mobile"),
        "scope", List.of("FREE_USER"),
        "authorities", List.of("ROLE_FREE_USER"),
        "exp", between(now(), now().plusDays(30)).getSeconds()
    );

    return Try.of(() -> mapper.writeValueAsString(tokenResponse))
        .getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new);
  }
}