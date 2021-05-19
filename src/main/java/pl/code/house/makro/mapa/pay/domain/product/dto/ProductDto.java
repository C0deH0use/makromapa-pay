package pl.code.house.makro.mapa.pay.domain.product.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Value;
import pl.code.house.makro.mapa.pay.domain.product.PointsOperationReason;

@Value
@Builder
public class ProductDto {

  Long id;
  int points;
  Set<PointsOperationReason> reasons;
}
