package pl.code.house.makro.mapa.pay.infrastructure;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.NEVER;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

  @Bean
  @Primary
  PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth,
      @Autowired PasswordEncoder passwordEncoder,
      @Value("${admin.auth.password}") String adminPassword) {
    Try.of(() -> auth.inMemoryAuthentication()
        .withUser("admin_aga")
        .password(passwordEncoder.encode(adminPassword))
        .roles("ADMIN"))
        .getOrElseThrow((exc) -> new IllegalStateException("Error when creating BASIC AUTH ADMIN", exc));
  }

  @Order(1)
  @Configuration
  public static class BasicAuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .antMatcher("/actuator/**")
          .authorizeRequests(authorize -> authorize.anyRequest().hasRole("ADMIN"))
          .httpBasic(withDefaults())
      ;
    }
  }

  @Order(5)
  @Configuration
  @EnableResourceServer
  @RequiredArgsConstructor
  public static class OAuth2SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ResourceServerProperties sso;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .csrf().disable()

          .antMatcher("/api/v1/**")
          .authorizeRequests().anyRequest().authenticated()

          .and()
          .sessionManagement().sessionCreationPolicy(NEVER)

          .and()
          .oauth2ResourceServer()

          .opaqueToken()
          .introspectionUri(sso.getTokenInfoUri())
          .introspectionClientCredentials(sso.getClientId(), sso.getClientSecret())
      ;
    }
  }
}
