package pl.code.house.makro.mapa.pay.domain.stripe;

import static java.util.stream.Collectors.toList;

import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.param.PriceListParams;
import com.stripe.param.PriceListParams.Builder;
import io.vavr.control.Try;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.PriceDto;

@Slf4j
@Service
class PriceService extends StripeBaseApi {

  PriceService(@Value("${payments.stripe.api-key}") String stripeApiKey) {
    super(stripeApiKey);
  }

  public List<PriceDto> findPrices() {
    Builder builder = PriceListParams.builder()
        .setActive(true);

    return Try.of(() -> Price.list(builder.build()))
        .map(PriceCollection::getData)
        .toJavaStream()
        .flatMap(List::stream)
        .map(PriceDto::from)
        .collect(toList());
  }

  public List<PriceDto> findPricesByProduct(String productId) {
    Builder builder = PriceListParams.builder()
        .setActive(true)
        .setProduct(productId);

    return Try.of(() -> Price.list(builder.build()))
        .map(PriceCollection::getData)
        .toJavaStream()
        .flatMap(List::stream)
        .map(PriceDto::from)
        .collect(toList());
  }

}
