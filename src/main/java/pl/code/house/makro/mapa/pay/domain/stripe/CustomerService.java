package pl.code.house.makro.mapa.pay.domain.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import io.vavr.control.Try;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.code.house.makro.mapa.pay.domain.stripe.dto.CustomerDto;
import pl.code.house.makro.mapa.pay.domain.user.UserInfoDto;
import pl.code.house.makro.mapa.pay.error.CustomerCreationException;

@Slf4j
@Service
class CustomerService extends StripeBaseApi {

  CustomerService(@Value("${payments.stripe.api-key}") String stripeApiKey) {
    super(stripeApiKey);
  }

  Optional<CustomerDto> findCustomerByEmail(String customerEmail) {
    CustomerListParams listParams = CustomerListParams.builder()
        .setEmail(customerEmail)
        .build();
    log.debug("Searching for Customer by email: {}", customerEmail);

    return Try.of(() -> Customer.list(listParams))
        .map(CustomerCollection::getData)
        .onFailure(StripeException.class, exc -> log.error("Exception during listing customer by email `{}` occurred", customerEmail, exc))
        .toJavaStream()
        .peek(customers -> log.debug("Found {} customer(s) listed under email: {}", customers.size(), customerEmail))
        .flatMap(List::stream)
        .map(this::toCustomerDto)
        .findFirst();
  }

  CustomerDto storeNewCustomer(UserInfoDto userInfo, String paymentMethodId) {
    CustomerCreateParams createParams = CustomerCreateParams.builder()
        .setEmail(userInfo.email())
        .setName(userInfo.name())
        .putMetadata("USER_ID", userInfo.sub())
        .setPaymentMethod(paymentMethodId)
        .addPreferredLocale("PL")
        .build();
    log.debug("Storing new Customer {} STRIPE Payment Gateway",
        createParams);

    return Try.of(() -> Customer.create(createParams))
        .onFailure(StripeException.class, exc -> log.error("Exception during `{}` customer creation occurred", userInfo.name(), exc))
        .toJavaStream()
        .map(this::toCustomerDto)
        .findFirst()
        .orElseThrow(() -> new CustomerCreationException("Unable to create new STRIPE Customer `%s`".formatted(userInfo.name())));
  }

  private CustomerDto toCustomerDto(Customer customer) {
    return new CustomerDto(customer.getId(), customer.getEmail(), customer.getCurrency(), customer.getDefaultSource());
  }
}
