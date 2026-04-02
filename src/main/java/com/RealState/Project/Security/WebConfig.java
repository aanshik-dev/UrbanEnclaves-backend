package com.RealState.Project.Security;
import jakarta.servlet.Filter;
import jakarta.validation.MessageInterpolator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.ContentHandler;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig {

    private final AuthFilter authFilter;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(csrfConfig->csrfConfig.disable())
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth->auth
//                        .requestMatchers("/public/**","/auth/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oAuth->oAuth.failureHandler(
                        (request, response, exception) ->{
                            log.error("Oauth2 error :{}",exception.getMessage());
                        }
                ).successHandler(oAuthSuccessHandler));
//               .formLogin(Customizer.withDefaults());
        return httpSecurity.build();
    }


}
