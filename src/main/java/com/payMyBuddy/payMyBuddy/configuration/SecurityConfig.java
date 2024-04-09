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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Configuration class responsible for configuring Spring Security settings.
 * It enables web security and defines authentication and authorization configurations.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    /**
     * Constructs a new SecurityConfig instance with the specified dependencies.
     *
     * @param customUserDetailsService the custom user details service used for user authentication
     * @param userRepository          the user repository used for accessing user data
     */
    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, UserRepository userRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
    }

    /**
     * Configures the security rules of the application.
     *
     * @param http the HttpSecurity object to configure HTTP security
     * @return the configured security filter chain to handle HTTP requests
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers("/","/login", "/register", "/error").permitAll()
                    .requestMatchers("/home", "/contact", "/profile", "/transfer").hasRole("USER")
                    .requestMatchers("/profile/**").hasRole("USER")
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
                .logout((logout) -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll());
        return http.build();
    }

    /**
     * Creates and returns a BCryptPasswordEncoder instance.
     *
     * @return a BCryptPasswordEncoder instance for encoding passwords
     */
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates and returns an AuthenticationProvider instance configured with a DaoAuthenticationProvider.
     * It sets the custom user details service and password encoder for authentication.
     *
     * @return an AuthenticationProvider instance configured with a DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(encoder());
        return daoAuthenticationProvider;
    }

    /**
     * Creates and returns an AuthenticationManager instance using the provided AuthenticationConfiguration.
     * It retrieves the authentication manager from the configuration.
     *
     * @param config the AuthenticationConfiguration used to obtain the AuthenticationManager
     * @return an AuthenticationManager instance
     * @throws Exception if an error occurs while retrieving the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return  config.getAuthenticationManager();
    }

    /**
     * Creates and returns an OAuth2UserService instance.
     * This method overrides the loadUser method to customize the behavior of loading OAuth2 users.
     * It loads the user based on the provided OAuth2UserRequest.
     * If the user exists in the UserRepository, it returns a modified OAuth2User object with user attributes.
     * If the user does not exist, it returns the original OAuth2User object.
     *
     * @return an OAuth2UserService instance
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new DefaultOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
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
                    return new DefaultOAuth2User(user.getAuthorities(), user.getAttributes(), "email");
                } else {
                    return user;
                }
            }
        };
    }

    /**
     * Creates and returns a GrantedAuthoritiesMapper instance.
     * This method defines a mapping function that maps the provided authorities to a set of GrantedAuthority objects.
     * It checks the type of each authority and creates a corresponding GrantedAuthority object with the appropriate role.
     *
     * @return a GrantedAuthoritiesMapper instance
     */
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
