package com.payMyBuddy.payMyBuddy.configuration;

import com.payMyBuddy.payMyBuddy.enums.RoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringWebSecurity {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.authorizeHttpRequests(auth -> {
//            auth.requestMatchers("/admin").hasRole(RoleType.ADMIN.name());
//            auth.requestMatchers("/user").hasRole(RoleType.USER.name());
//            auth.anyRequest().authenticated();}
//                ).formLogin(Customizer.withDefaults()).build();
//    }

    // TODO : Delete these false users created for testing !
    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user")
                .password(encoder().encode("user"))
                .roles(RoleType.USER.name())
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder().encode("admin"))
                .roles(RoleType.USER.name(), RoleType.ADMIN.name())
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
