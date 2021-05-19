package pl.code.house.makro.mapa.pay;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.restassured.http.Header;

public class AuthenticationToken {

  public static Header getAuthenticationHeader() {
    return new Header(AUTHORIZATION, "Bearer b146b422-475c-4beb-9e9c-4e33e2288b08");
  }

  public static final String SYSTEM_BASIC_TOKEN = "bWFrcm9tYXBhLWJhY2tlbmQ6c2VjcmV0";
  public static final String SYSTEM_BEARER_TOKEN = "7387f60e-8ee1-4d54-bd43-4e98a170e214";
}
