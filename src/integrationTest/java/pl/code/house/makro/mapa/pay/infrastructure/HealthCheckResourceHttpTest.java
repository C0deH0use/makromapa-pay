package pl.code.house.makro.mapa.pay.infrastructure;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class HealthCheckResourceHttpTest {

  @Value("${info.app.name}")
  String appTitle;

  @Value("${info.app.description}")
  String appDesc;

  @Autowired
  WebApplicationContext context;

  @BeforeEach
  void setup() {
    webAppContextSetup(context, springSecurity());
  }

  @Test
  @DisplayName("should display health status")
  void shouldDisplayHealthStatus() {
    //given
    given()
        .contentType(JSON)

        .when()
        .get("/actuator/health")

        .then()
        .log().ifValidationFails()
        .statusCode(OK.value())
    ;
  }

  @Test
  @DisplayName("should display info status")
  void shouldDisplayInfoStatus() {
    //given
    given()
        .contentType(JSON)
        .auth().with(httpBasic("admin_aga", "mysecretpassword"))

        .when()
        .get("/actuator/info")

        .then()
        .log().all(true)
        .statusCode(OK.value())

        .body("app.name", equalTo(appTitle))
        .body("app.description", equalTo(appDesc))
        .body("build.artifact", equalTo("makromapa-pay"))
        .body("build.name", equalTo("makromapa-pay"))
        .body("build.group", equalTo("pl.code.house.makro.mapa.pay"))
    ;
  }

  @Test
  @DisplayName("should return UNAUTHORIZED when asking for info status without credentials")
  void shouldReturnUnAuthorizedWhenAskingForInfoStatusWithoutCredentials() {
    //given
    given()
        .contentType(JSON)

        .when()
        .get("/actuator/info")

        .then()
        .statusCode(UNAUTHORIZED.value())
    ;
  }
}