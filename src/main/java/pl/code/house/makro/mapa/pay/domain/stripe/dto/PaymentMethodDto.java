package pl.code.house.makro.mapa.pay.domain.stripe.dto;

import com.stripe.model.PaymentMethod;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentMethodDto {

  String id;
  String type;
  PaymentMethod.Card card;

  public static PaymentMethodDto from(PaymentMethod paymentMethod) {
    return PaymentMethodDto.builder()
        .id(paymentMethod.getId())
        .type(paymentMethod.getType())
        .card(paymentMethod.getCard())
        .build();
  }
}
