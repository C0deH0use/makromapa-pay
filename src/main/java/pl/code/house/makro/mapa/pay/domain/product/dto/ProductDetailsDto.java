package pl.code.house.makro.mapa.pay.domain.product.dto;

import lombok.Builder;
import lombok.Value;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.StripeProductDto;

@Value
@Builder
public class ProductDetailsDto {

  ProductDto productDto;
  StripeProductDto stripProduct;
}
