package pl.code.house.makro.mapa.pay.error;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class CreatePaymentIntentException extends RuntimeException {

  public CreatePaymentIntentException(Throwable exc) {
    super(exc);
  }
}
