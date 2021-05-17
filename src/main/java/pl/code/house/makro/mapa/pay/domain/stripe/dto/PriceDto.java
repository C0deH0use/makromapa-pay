package pl.code.house.makro.mapa.pay.domain.stripe.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stripe.model.Price;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PriceDto {

  String id;
  String billingScheme;
  String currency;
  String product;
  String type;
  String interval;
  Long unitAmount;

  @JsonCreator
  public PriceDto(
      @JsonProperty("id") String id,
      @JsonProperty("billingScheme") String billingScheme,
      @JsonProperty("currency") String currency,
      @JsonProperty("product") String product,
      @JsonProperty("type") String type,
      @JsonProperty("interval") String interval,
      @JsonProperty("unitAmount") Long unitAmount) {
    this.id = id;
    this.billingScheme = billingScheme;
    this.currency = currency;
    this.product = product;
    this.type = type;
    this.interval = interval;
    this.unitAmount = unitAmount;
  }

  public static PriceDto from(Price price) {
    return PriceDto.builder()
        .id(price.getId())
        .billingScheme(price.getBillingScheme())
        .unitAmount(price.getUnitAmount())
        .currency(price.getCurrency())
        .product(price.getProduct())
        .type(price.getType())
        .interval(price.getRecurring().getInterval())
        .type(price.getRecurring().getUsageType())
        .build();
  }
}
