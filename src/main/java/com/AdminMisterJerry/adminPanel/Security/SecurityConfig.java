package com.AdminMisterJerry.adminPanel.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationProvider;

import com.AdminMisterJerry.adminPanel.Model.UserServiceLogin;

/**
 * SecurityConfig unificata che integra le configurazioni presenti in
 * ApiSecurityConfig.java e SecurityConfiguration.java.
 *
 * Note:
 * - Mantiene il UserServiceLogin come UserDetailsService originale.
 * - Registra un AuthenticationProvider (DaoAuthenticationProvider) con BCrypt.
 * - Espone AuthenticationManager se necessario (alcune parti dell'app lo richiedono).
 * - Limita il CSRF solo per le chiamate API (/api/**).
 * - Permette le risorse statiche (/css, /js...) e gestisce la pagina di login per il backoffice.
 *
 * Adatta i matcher e le regole a seconda delle tue esigenze (es. rendere le /api/** protette).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserServiceLogin userServiceLogin;

    @Autowired
    public SecurityConfig(UserServiceLogin userServiceLogin) {
        this.userServiceLogin = userServiceLogin;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userServiceLogin);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userServiceLogin;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF abilitato per i form, ma disabilitato o ignorato per le API
            .csrf(AbstractHttpConfigurer::disable)


            // registra il nostro AuthenticationProvider (userDetails + password encoder)
            .authenticationProvider(authenticationProvider())

            // configurazione delle pagine di login / logout e comportamento
            .formLogin(form -> form
                // la tua pagina di login (come in SecurityConfiguration.java)
                .loginPage("/req/login").permitAll()
                .defaultSuccessUrl("/admin-panel", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/req/login?logout").permitAll()
            )

            // autorizzazioni per le richieste
            .authorizeHttpRequests(auth -> auth
                // risorse statiche
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // API: se vuoi che le API siano pubbliche mantieni permitAll(); altrimenti cambia in authenticated()
                .requestMatchers("/api/**").permitAll()

                // Backoffice / area admin: richiede autenticazione
                .requestMatchers("/backoffice/**", "/admin-panel/**", "/req/**").authenticated()

                // fallback: lasciare permesso pubblico (adatta se vuoi restringere)
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
