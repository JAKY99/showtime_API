package com.m2i.showtime.yak.Jwt.Config;

import com.m2i.showtime.yak.Jwt.JwtConfig;
import com.m2i.showtime.yak.Jwt.JwtTokenVerifier;
import com.m2i.showtime.yak.Jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.m2i.showtime.yak.Security.CustomAuthenticationEntryPoint;
import com.m2i.showtime.yak.Service.User.UserAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final DataSource dateSource;
    private final UserAuthService userAuthService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/v1/**"),new AntPathRequestMatcher("management/**")
    );

    public WebSecurityConfig(PasswordEncoder passwordEncoder,
                             DataSource dateSource,
                             UserAuthService userAuthService,
                             SecretKey secretKey,
                             JwtConfig jwtConfig, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.passwordEncoder = passwordEncoder;
        this.dateSource = dateSource;
        this.userAuthService = userAuthService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable() // Not needed since we are using JWT
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/v*/registration/**", "/api/v*/login/**","/websocket/**","/api/v*/elasticsearch/**","/api/v1/health/**","/api/v1/version/**","api/v1/login/google").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userAuthService);
        return provider;
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //List<String> allowOrigins = Arrays.asList("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Refresh"));
        configuration.addExposedHeader("Content-Type,X-Requested-With,Accept,Authorization,Refresh,Origin,Access-Control-Request-Method,Access-Control-Request-Headers");
        //in case authentication is enabled this flag MUST be set, otherwise CORS requests will fail
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
