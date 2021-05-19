package pl.code.house.makro.mapa.pay.domain.stripe;

import static java.util.stream.Collectors.toList;

import com.stripe.model.Product;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PriceDto;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.StripeProductDto;

@Service
@RequiredArgsConstructor
public class StripProductFacade {

  private final PriceService priceService;
  private final ProductService productService;

  public List<StripeProductDto> getProductPricesFor(String... productNames) {
    return productService.findProductsBy(productNames)
        .stream()
        .map(product -> Tuple.of(product, priceService.findPricesByProduct(product.getId())))
        .map(this::mapToStripeProduct)
        .collect(toList());
  }

  private StripeProductDto mapToStripeProduct(Tuple2<Product, List<PriceDto>> productTuple) {
    return StripeProductDto.builder()
        .id(productTuple._1().getId())
        .name(productTuple._1().getName())
        .type(productTuple._1().getType())
        .url(productTuple._1().getUrl())
        .metadata(productTuple._1().getMetadata())
        .productPrices(productTuple._2())
        .build();
  }
}
