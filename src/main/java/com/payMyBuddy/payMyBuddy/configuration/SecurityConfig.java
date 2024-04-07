package com.payMyBuddy.payMyBuddy.configuration;

import com.payMyBuddy.payMyBuddy.model.User;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers("/","/login", "/register", "/error").permitAll()
                    .requestMatchers("/home", "/contact", "/profile", "/transfer").hasRole("USER")
                    .requestMatchers("/user/**").hasRole("USER")
                    .requestMatchers("/profile/**").hasRole("USER")
                    .requestMatchers("/bank-account/**").hasRole("USER")
                    .requestMatchers("/transactions/**").hasRole("USER")
                    .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home")
                        .usernameParameter("email")
                        .passwordParameter("password"))
                .oauth2Login(
                        oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(grantedAuthoritiesMapper())
                                .userService(oAuth2UserService()))
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/test", true))
                .logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(encoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return  config.getAuthenticationManager();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new DefaultOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//                Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
//                DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
//                return defaultService.loadUser(userRequest);
                OAuth2User user = super.loadUser(userRequest);
                String email = "";
                Map<String, Object> userAttributes = user.getAttributes();
                for (Map.Entry<String, Object> entry : userAttributes.entrySet()) {
                    if ("email".equals(entry.getKey())) {
                        email = (String) entry.getValue();
                        break;
                    }
                }

                Optional<User> optionalUser = userRepository.findByEmail(email);
                if (optionalUser.isPresent()) {
                    User existingUser = optionalUser.get();
                    return new DefaultOAuth2User(user.getAuthorities(), user.getAttributes(), "email");
                } else {
                    return user;
                }
            }
        };
    }

    private GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach((authority) -> {
                GrantedAuthority mappedAuthority;

                if (authority instanceof OidcUserAuthority userAuthority) {
                    mappedAuthority = new OidcUserAuthority(
                            "ROLE_USER", userAuthority.getIdToken(), userAuthority.getUserInfo());
                } else if (authority instanceof OAuth2UserAuthority userAuthority) {
                    mappedAuthority = new OAuth2UserAuthority(
                            "ROLE_USER", userAuthority.getAttributes());
                } else {
                    mappedAuthority = authority;
                }

                mappedAuthorities.add(mappedAuthority);
            });

            return mappedAuthorities;
        };
    }
}