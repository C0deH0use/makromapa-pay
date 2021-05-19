package pl.code.house.makro.mapa.pay.domain.stripe;

import static java.util.stream.Collectors.toList;

import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.param.ProductListParams;
import com.stripe.param.ProductListParams.Builder;
import io.vavr.control.Try;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class ProductService extends StripeBaseApi {

  ProductService(@Value("${payments.stripe.api-key}") String stripeApiKey) {
    super(stripeApiKey);
  }

  List<Product> findProductsBy(String... productKeys) {
    Builder listParams = ProductListParams.builder()
        .setActive(true);

    return Try.of(() -> Product.list(listParams.build()))
        .map(ProductCollection::getData)
        .map(List::stream)
        .getOrElse(Stream.empty())
        .filter(product -> List.of(productKeys).contains(product.getMetadata().get("PRODUCT_KEY")))
        .collect(toList());
  }

  Product getProductById(String productId) {
    return Try.of(() -> Product.retrieve(productId))
        .getOrNull();
  }
}
