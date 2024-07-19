package fr.projet.diginamic.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(registry -> {
//                    registry.requestMatchers("/", "/api/auth/login", "/api/auth/register").permitAll();
//                    registry.anyRequest().authenticated();
//                }).formLogin(AbstractAuthenticationFilterConfigurer::permitAll).build();
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder().username("user")
                .password("$2y$10$ZULHEXmst.Hp3Eek9m63oehTvpmiDE.uHLceyw9HEchxRosk367xe").roles("USER").build();
        UserDetails manager = User.builder().username("manager")
                .password("$2y$10$YVnNpnyqMx2VMCWfARW8Vu8hlfHfqugBuXTPE3ay7ZE0Jj2yRHT1y").roles("MANAGER").build();
        UserDetails admin = User.builder().username("admin")
                .password("$2y$10$7a1gj7tO86Hjwgmo5O4oi.VloLAWK.A3rbYkWtOLqhpHFFrGRBP8a").roles("ADMIN").build();

        return new InMemoryUserDetailsManager(user, manager, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
        

    	@Bean
    	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    		http.authorizeHttpRequests(request ->request
    				.anyRequest().permitAll()
    				)
    		.httpBasic(Customizer.withDefaults()).
    		formLogin(Customizer.withDefaults());
    		return http.build();
    		
    	}
    
}
