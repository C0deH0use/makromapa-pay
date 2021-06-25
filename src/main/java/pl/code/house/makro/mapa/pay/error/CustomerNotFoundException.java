package pl.code.house.makro.mapa.pay.error;

import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;

import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(PRECONDITION_REQUIRED)
public class CustomerNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -8580951655377272469L;

  public CustomerNotFoundException(String message) {
    super(message);
  }
}
