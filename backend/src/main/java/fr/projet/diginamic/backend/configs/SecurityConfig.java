package fr.projet.diginamic.backend.configs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import fr.projet.diginamic.backend.middlewares.JWTAuthMiddleware;
import fr.projet.diginamic.backend.services.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * User service
     */
    @Autowired
    private UserService userService;

    /**
     * JWT auth middleware
     */
    @Autowired
    private JWTAuthMiddleware jwtAuthMidlleware;

    private static final String[] SWAGGER_WHITELIST = {
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    /**
     * Security filter chain
     *
     * @param http - the http security
     * @throws Exception exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {

                    registry
                            .requestMatchers(
                                    "/api/auth/login",
                                    "/api/auth/register")
                            .permitAll();
                    registry
                            .requestMatchers(SWAGGER_WHITELIST)
                            .permitAll();
                    registry
                            .requestMatchers("/api/auth/user")
                            .hasAnyRole("USER", "MANAGER", "ADMIN");
                    registry
                            .requestMatchers("/api/auth/manager")
                            .hasAnyRole("MANAGER", "ADMIN");
                    registry
                            .requestMatchers("/api/auth/admin").hasRole("ADMIN");
                    registry
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthMidlleware, UsernamePasswordAuthenticationFilter.class);
        // http.logout(authz -> authz
        // .deleteCookies("JSESSIONID")
        // .logoutRequestMatcher(new AntPathRequestMatcher("/logout")));

        return http.build();
    }

    /**
     * Cors configuration source
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * User details service
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    /**
     * Authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    /**
     * Password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
