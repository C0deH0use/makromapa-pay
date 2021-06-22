package pl.code.house.makro.mapa.pay;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.restassured.http.Header;

public class AuthenticationToken {

  public static final String USER_SUB = "b146b422-475c-4beb-9e9c-4e33e2288b08";
  public static final String ADMIN_SUB = "aa6641c1-e9f4-417f-adf4-f71accc470cb";

  public static final String SYSTEM_BASIC_TOKEN = "bWFrcm9tYXBhLWJhY2tlbmQ6c2VjcmV0";
  public static final String SYSTEM_BEARER_TOKEN = "7387f60e-8ee1-4d54-bd43-4e98a170e214";

  public static Header getAuthenticationHeader() {
    return new Header(AUTHORIZATION, "Bearer 10c7fc72-64f6-4c9a-af2b-a5d33c65ecf3");
  }

  public static Header getAdminAuthenticationHeader() {
    return new Header(AUTHORIZATION, "Bearer a0942e10-726b-4598-82fc-bd8537bbded4");
  }
}
