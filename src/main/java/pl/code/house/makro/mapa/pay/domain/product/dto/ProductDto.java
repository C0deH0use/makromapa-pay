package pl.code.house.makro.mapa.pay.domain.product.dto;

import lombok.Builder;
import lombok.Value;
import pl.code.house.makro.mapa.pay.domain.product.PointsOperationReason;

@Value
@Builder
public class ProductDto {

  Long id;
  String name;
  int points;
  PointsOperationReason reason;
}
