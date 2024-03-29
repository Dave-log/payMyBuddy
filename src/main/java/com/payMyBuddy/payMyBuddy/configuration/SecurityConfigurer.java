package com.payMyBuddy.payMyBuddy.configuration;

import com.payMyBuddy.payMyBuddy.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfigurer(CustomUserDetailsService customUserDetailsService) {
        this.userDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers(HttpMethod.POST, "/users/add-buddy/**").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/users/remove-buddy/**").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/bank-account/**").hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, "/bank-account/**").hasRole("USER")
                    .requestMatchers(HttpMethod.POST, "/transaction/**").hasRole("USER")
                    .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(encoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return  config.getAuthenticationManager();
    }
}
