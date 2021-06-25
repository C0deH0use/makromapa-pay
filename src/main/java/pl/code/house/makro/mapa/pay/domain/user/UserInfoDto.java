package pl.code.house.makro.mapa.pay.domain.user;

import java.io.Serializable;
import java.util.Map;

public record UserInfoDto(String sub, String name, String surname, String nickname, String email, String picture, Boolean enabled,
                          Integer points) implements Serializable {

  public static UserInfoDto toDto(Map<String, Object> detailsMap) {
    return new UserInfoDto(
        (String) detailsMap.get("sub"),
        (String) detailsMap.get("name"),
        (String) detailsMap.get("surname"),
        (String) detailsMap.get("nickname"),
        (String) detailsMap.get("email"),
        (String) detailsMap.get("picture"),
        (Boolean) detailsMap.get("enabled"),
        (Integer) detailsMap.get("points")
    );
  }
}
