package pl.code.house.makro.mapa.pay.infrastructure;

import static org.springframework.boot.actuate.health.Status.UP;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
class CustomHealthIndicator {

  private final HealthEndpoint healthEndpoint;
  private final InfoEndpoint infoEndpoint;

  @GetMapping("/health")
  ResponseEntity<Void> checkHealth() {
    final Status appStatus = healthEndpoint.health().getStatus();

    if (UP.equals(appStatus)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/info")
  ResponseEntity<Map<String, Object>> getAppInfo() {
    return ResponseEntity.ok(infoEndpoint.info());
  }

}
