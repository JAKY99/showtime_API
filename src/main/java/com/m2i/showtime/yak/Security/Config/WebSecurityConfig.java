package com.m2i.showtime.yak.Security.Config;

import com.m2i.showtime.yak.Service.User.UserAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final DataSource dateSource;
    private final UserAuthService userAuthService;

    public WebSecurityConfig(PasswordEncoder passwordEncoder,
                             DataSource dateSource,
                             UserAuthService userAuthService) {
        this.passwordEncoder = passwordEncoder;
        this.dateSource = dateSource;
        this.userAuthService = userAuthService;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dateSource);
        return db;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable() //TODO: Enable csrf for production to prevent assholes breaking our API
                .authorizeRequests()
                .antMatchers("/api/v*/registration/**", "/api/v*/login/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                    .passwordParameter("password") //the name we need to put in the <input name="password"/>
                    .usernameParameter("email")
                .and()
                .rememberMe()
                    .tokenRepository(persistentTokenRepository())
                    .key("a9a1ef1c-20a9-428a-801e-d15ca96bc2a8")
                    .rememberMeParameter("remember-me")
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userAuthService);
        return provider;
    }
}
