package pl.code.house.makro.mapa.pay.error;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class CreatePaymentIntentException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 4378957945856264284L;

  public CreatePaymentIntentException(Throwable exc) {
    super(exc);
  }
}
