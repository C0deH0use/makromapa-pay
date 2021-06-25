package pl.code.house.makro.mapa.pay.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class CustomerCreationException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 6998280302825820133L;

  public CustomerCreationException(String message) {
    super(message);
  }
}
