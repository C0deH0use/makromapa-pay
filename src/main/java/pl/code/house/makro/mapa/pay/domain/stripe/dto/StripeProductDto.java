package pl.code.house.makro.mapa.pay.domain.stripe.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StripeProductDto {

  String id;
  String name;
  String url;
  String type;
  Map<String, String> metadata;
  List<PriceDto> productPrices;
}
