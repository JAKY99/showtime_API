package com.m2i.showtime.yak.Jwt;

import com.google.common.net.HttpHeaders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
@NoArgsConstructor
public class JwtConfig {

    private String tokenSecretKey;
    private String tokenPrefix;
    @Value(value = "${application.jwt.tokenExpirationAfterDays}")
    private Integer tokenExpirationAfterDays;
    @Value(value = "${application.jwt.tokenExpirationAfterMinutes}")
    private Integer tokenExpirationAfterMinutes;

    public JwtConfig(String tokenSecretKey, String tokenPrefix, Integer tokenExpirationAfterDays, Integer tokenExpirationAfterMinutes) {
        this.tokenSecretKey = tokenSecretKey;
        this.tokenPrefix = tokenPrefix;
        this.tokenExpirationAfterDays = tokenExpirationAfterDays;
        this.tokenExpirationAfterMinutes = tokenExpirationAfterMinutes;
    }

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }

    public String getRefreshHeader(){
        return HttpHeaders.REFRESH;
    }
}
