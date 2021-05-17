package pl.code.house.makro.mapa.pay.domain.product;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.code.house.makro.mapa.pay.domain.product.dto.ProductDetailsDto;
import pl.code.house.makro.mapa.pay.domain.stripe.StripProductFacade;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.StripeProductDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductFacade {

  private final ProductRepository productRepository;
  private final StripProductFacade productFacade;

  public List<ProductDetailsDto> findAll() {
    return productRepository.findAll()
        .stream()
        .map(this::getProductDetails)
        .flatMap(List::stream)
        .collect(toList());
  }

  public List<ProductDetailsDto> findById(Long productId) {
    return productRepository.findById(productId)
        .map(this::getProductDetails)
        .orElseGet(Collections::emptyList);
  }

  private List<ProductDetailsDto> getProductDetails(Product product) {
    List<StripeProductDto> stripeProducts = productFacade.getProductPricesFor(product.getName());
    if(stripeProducts.isEmpty()) {
      return singletonList(ProductDetailsDto.builder()
          .productDto(product.toDto())
          .build());
    }

    return stripeProducts
        .stream()
        .map(stripProductDto -> ProductDetailsDto.builder()
            .productDto(product.toDto())
            .stripProduct(stripProductDto)
            .build()
        )
        .collect(toList());
  }
}