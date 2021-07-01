package pl.code.house.makro.mapa.pay.domain.product;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
class ProductResourceHttpTest {

  @Autowired
  private WebApplicationContext context;

  @BeforeEach
  void setUp() {
    webAppContextSetup(context, springSecurity());
  }

  @Test
  @DisplayName("should return with all products and their Stripe details")
  void shouldReturnWithAllProductsAndTheirStripeDetails() {
    //given
    RestAssuredMockMvc.given()
        .header(getAuthenticationHeader())
        .contentType(ContentType.JSON)

        .when()
        .get("/api/v1/product")

        .then()
        .log().all(true)
        .status(OK)

        .body("$", hasSize(4))
        .body("id", everyItem(notNullValue(Integer.class)))
        .body("name", hasItems("APPROVED_DISH_PROPOSAL","ads_removal", "sub_premium", "DISABLE_ADS"))
        .body("points", hasItems(400, 0, 0, 100))
        .body("reason", hasItems("USE", "EARN"))
    ;
  }
}