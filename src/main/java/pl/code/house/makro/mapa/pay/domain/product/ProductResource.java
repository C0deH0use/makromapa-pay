package pl.code.house.makro.mapa.pay.domain.product;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.code.house.makro.mapa.pay.domain.product.dto.ProductDetailsDto;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/product", produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
class ProductResource {

  private final ProductFacade facade;

  @GetMapping
  List<ProductDetailsDto> getProducts(@AuthenticationPrincipal String principal) {
    log.info("User: {} requested all points products that are currently available for MakroMapa", principal);

    return facade.findAll();
  }
}
