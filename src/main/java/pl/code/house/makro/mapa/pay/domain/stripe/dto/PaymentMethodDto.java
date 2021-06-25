package pl.code.house.makro.mapa.pay.domain.stripe.dto;

import com.stripe.model.PaymentMethod;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentMethodDto {

  String id;
  String type;
  String customerId;
  Boolean livemode;
  Long created;
  PaymentMethod.Card card;
  Map<String, String> metadata;

  public static PaymentMethodDto from(PaymentMethod paymentMethod) {
    return PaymentMethodDto.builder()
        .id(paymentMethod.getId())
        .type(paymentMethod.getType())
        .customerId(paymentMethod.getCustomer())
        .livemode(paymentMethod.getLivemode())
        .created(paymentMethod.getCreated())
        .card(paymentMethod.getCard())
        .metadata(paymentMethod.getMetadata())
        .build();
  }
}
