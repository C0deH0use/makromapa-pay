package pl.code.house.makro.mapa.pay.error.handler;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

  private final ErrorHttpStatusResolver statusResolver = new ErrorHttpStatusResolver();

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> respondWithError(Exception ex, HandlerMethod handlerMethod) {
    final ErrorMessage errorMessage = ErrorMessage.from(ex);
    logError(ex, errorMessage.getUniqueErrorId(), handlerMethod);

    HttpStatus httpStatus = statusResolver.statusOf(ex)
        .orElse(INTERNAL_SERVER_ERROR);

    return status(httpStatus).contentType(APPLICATION_JSON).body(errorMessage);
  }

  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, InvalidFormatException.class})
  public ResponseEntity<Object> handleBadRequestException(Exception ex, HandlerMethod handlerMethod) {
    final ErrorMessage errorMessage = ErrorMessage.from(ex);
    logError(ex, errorMessage.getUniqueErrorId(), handlerMethod);
    return badRequest().contentType(APPLICATION_JSON).body(errorMessage);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, HandlerMethod handlerMethod) {
    final ErrorMessage errorMessage = ErrorMessage.from(ex);
    logError(ex, errorMessage.getUniqueErrorId(), handlerMethod);
    Map<String, Object> body = Map.of("errors", ex.getConstraintViolations()
        .stream()
        .map(violation -> format("PropertyPath=%s, %s, send value = `%s`", violation.getPropertyPath(), violation.getMessage(), violation.getInvalidValue()))
        .collect(toList()));

    return badRequest().contentType(APPLICATION_JSON).body(body);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    final ErrorMessage errorMessage = ErrorMessage.from(ex);
    logError(ex, errorMessage.getUniqueErrorId());

    //Get all errors
    Map<String, Object> body = Map.of("errors", ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(toList()));

    return badRequest().contentType(APPLICATION_JSON).body(body);
  }

  private void logError(Exception ex, UUID errorId, HandlerMethod handlerMethod) {
    log.debug("Exception {} with message '{}' and id '{}' occurs in {} in method {}({})",
        ex.getClass().getSimpleName(),
        ex.getMessage(),
        errorId,
        handlerMethod.getBeanType().getSimpleName(),
        handlerMethod.getMethod().getName(),
        Arrays.toString(handlerMethod.getMethod().getParameters())
    );

    logError(ex, errorId);
  }

  private void logError(Exception ex, UUID errorId) {
    log.error("Exception id '{}' - {}", ex.getMessage(), errorId, ex);
  }
}
