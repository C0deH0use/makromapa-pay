package pl.code.house.makro.mapa.pay.domain.stripe.product;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import com.stripe.Stripe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.context.WebApplicationContext;
import pl.code.house.makro.mapa.pay.MockOAuth2User;

@SpringBootTest
@MockOAuth2User
class ProductResourceHttpTest {

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
  @DisplayName("should return with all products and their Stripe details")
  void shouldReturnWithAllProductsAndTheirStripeDetails() {
    //given
    stubFor(get(urlEqualTo("/v1/products?active=true"))
        .willReturn(aResponse()
            .withBodyFile("stripe/active_products.json"))
    );
    stubFor(get(urlEqualTo("/v1/prices?product=prod_JLJ8zD5OU23DjR&active=true"))
        .willReturn(aResponse()
            .withBodyFile("stripe/disable_ads_product_prices.json"))
    );
    stubFor(get(urlEqualTo("/v1/prices?product=prod_JQM9POpISLwEl0&active=true"))
        .willReturn(aResponse()
            .withBodyFile("stripe/premium_product_prices.json"))
    );

    given()
        .header(getAuthenticationHeader())
        .contentType(JSON)

        .when()
        .get("/api/v1/product")

        .then()
        .log().ifValidationFails()
        .status(OK)

        .body("$", hasSize(3))
        .body("productDto.id", hasItems(1001, 1002, 1000))
        .body("productDto.points", hasItems(400, 1000, 100))
        .body("productDto.reasons.flatten()", hasItems("USE", "EARN"))

        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.id", notNullValue())
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.name", hasItems("Test Disable Ads", "TEST Premium"))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.url", notNullValue())
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.type", everyItem(equalTo("service")))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.metadata", everyItem(hasKey("PRODUCT_KEY")))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.metadata", hasItems(hasValue("DISABLE_ADS"), hasValue("PREMIUM")))

        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices", hasSize(2))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.id.flatten()", everyItem(notNullValue()))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.product.flatten()", everyItem(notNullValue()))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.currency.flatten()", everyItem(equalTo("pln")))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.type.flatten()", everyItem(equalTo("licensed")))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.interval.flatten()", hasItems(equalTo("week"), equalTo("month")))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.unitAmount.flatten()", hasSize(3))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.unitAmount.flatten()", hasItems(greaterThanOrEqualTo(200), lessThanOrEqualTo(2500)))
        .body("findAll{ it.productDto.reasons.contains(\"USE\")}.stripProduct.productPrices.billingScheme.flatten()", everyItem(equalTo("per_unit")))
    ;
  }
}