package pl.code.house.makro.mapa.pay.domain.user;

import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.DefaultUserInfoRestTemplateFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

@Configuration
class UserInfoConfiguration {

  @Bean
  public UserInfoFacade userInfoTokenServices(ResourceServerProperties sso, UserInfoRestTemplateFactory restTemplateFactory) {
    UserInfoTokenServices services = new UserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
    services.setRestTemplate(restTemplateFactory.getUserInfoRestTemplate());
    services.setTokenType(sso.getTokenType());

    return new UserInfoFacade(services);
  }

  @Bean
  @ConditionalOnMissingBean
  UserInfoRestTemplateFactory userInfoRestTemplateFactory(
      ObjectProvider<List<UserInfoRestTemplateCustomizer>> customizers,
      ObjectProvider<OAuth2ProtectedResourceDetails> details,
      ObjectProvider<OAuth2ClientContext> oauth2ClientContext) {
    return new DefaultUserInfoRestTemplateFactory(customizers, details,
        oauth2ClientContext);
  }

}
