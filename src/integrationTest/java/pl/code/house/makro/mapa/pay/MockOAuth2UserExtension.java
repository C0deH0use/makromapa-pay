package pl.code.house.makro.mapa.pay;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MockOAuth2UserExtension implements BeforeEachCallback, Extension {

  private static final String USER_SUB = "aa6641c1-e9f4-417f-adf4-f71accc470cb";
  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void beforeEach(ExtensionContext context) {
    mockUserInfo();
  }

  private void mockUserInfo() {
    stubFor(post(urlEqualTo("/oauth/check_token"))
        .willReturn(okJson(tokenCheckResponse()))
    );
  }

  String tokenCheckResponse() {
    Map<String, Object> tokenResponse = Map.of(
        "active", true,
        "user_name", USER_SUB,
        "aud", List.of("makromapa-mobile"),
        "scope", List.of("USER"),
        "authorities", List.of("ROLE_FREE_USER"),
        "exp", between(now(), now().plusDays(30)).getSeconds()
    );

    return Try.of(() -> mapper.writeValueAsString(tokenResponse))
        .getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new);
  }
}
