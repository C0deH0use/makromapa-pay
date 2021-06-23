package pl.code.house.makro.mapa.pay.domain.stripe;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.stripe.model.Product;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PriceDto;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
//  @PreAuthorize("#oauth2.hasAuthority('ROLE_ADMIN')")
@RequestMapping(path = "/api/v1/stripe",
    produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
class PriceResource {

  private final ProductService productService;
  private final PriceService priceService;

  @GetMapping("/price")
  List<PriceDto> findPricesForProductName(
      @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
      @RequestParam(value = "productName", required = false) String productName) {

    log.info("Request to find all known prices, send by admin: {}", principal.<String>getAttribute("user_name"));

    if (isNotBlank(productName)) {
      log.info("Request to find all prices linked to product by productName `{}`", productName);
      return productService.findProductsBy(productName)
          .stream()
          .map(Product::getId)
          .map(priceService::findPricesByProduct)
          .flatMap(List::stream)
          .collect(toList());
    }

    return priceService.findPrices();
  }
}
