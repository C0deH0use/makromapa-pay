package pl.code.house.makro.mapa.pay.infrastructure;

import static org.springframework.security.config.http.SessionCreationPolicy.NEVER;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

  private final ResourceServerProperties sso;
  private final SecurityProperties securityProperties;

  @Bean
  @Primary
  PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()

        .antMatcher("/api/v1/**")
        .authorizeRequests(req -> req.anyRequest().authenticated())

        .sessionManagement().sessionCreationPolicy(NEVER)

        .and()
        .oauth2ResourceServer()

        .opaqueToken()
        .introspectionUri(sso.getTokenInfoUri())
        .introspectionClientCredentials(sso.getClientId(), sso.getClientSecret())
    ;
  }

  @Order(50)
  @Configuration
  @RequiredArgsConstructor
  static class ActuatorsAuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http
          .csrf().disable()

          .antMatcher("/actuator/**")

          .authorizeRequests(req -> req
              .antMatchers("/actuator/health").permitAll()
              .anyRequest().hasRole("ADMIN")
          )
          .httpBasic()
      ;
    }
  }
}
