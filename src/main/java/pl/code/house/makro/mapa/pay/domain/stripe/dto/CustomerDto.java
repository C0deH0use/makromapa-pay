package pl.code.house.makro.mapa.pay.domain.stripe.dto;

public record CustomerDto(String id, String email, String currency, String paymentMethodId) {

}
