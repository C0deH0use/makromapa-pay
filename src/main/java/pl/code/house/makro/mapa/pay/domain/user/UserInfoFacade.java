package pl.code.house.makro.mapa.pay.domain.user;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.removeIgnoreCase;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@RequiredArgsConstructor
public class UserInfoFacade {

  private static final String BEARER_TOKEN = "Bearer ";
  private final UserInfoTokenServices userInfoTokenServices;

  public UserInfoDto userInfo(String bearerAccessToken) {
    if (!contains(bearerAccessToken, BEARER_TOKEN)) {
      throw new IllegalArgumentException("Required Bearer Token to fetch UserInfo");
    }
    String accessToken = removeIgnoreCase(bearerAccessToken, BEARER_TOKEN);

    OAuth2Authentication userInfo = userInfoTokenServices.loadAuthentication(accessToken);
    Map<String, Object> details = (Map<String, Object>) userInfo.getUserAuthentication().getDetails();

    return UserInfoDto.toDto(details);
  }

}
