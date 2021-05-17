package pl.code.house.makro.mapa.pay.domain.stripe;

import com.stripe.Stripe;

abstract class StripeBaseApi {

  protected StripeBaseApi(String stripeApiKey) {
    Stripe.apiKey = stripeApiKey;
  }

}
