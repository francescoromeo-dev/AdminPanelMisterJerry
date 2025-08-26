package com.AdminMisterJerry.adminPanel.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.AdminMisterJerry.adminPanel.Model.UserServiceLogin;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    //Carico gli utenti dal database
    private final UserServiceLogin userServiceLogin;

    @Bean
    //Questo dice a spring di usare UserServiceLogin che implementa UserDetailsService quando deve cercare un utente
    public UserDetailsService userDetailsService(){
        return userServiceLogin;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userServiceLogin);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(httpForm -> {
                //Imposto la pagina di login
                httpForm.loginPage("/req/login").permitAll();
                //Indirizzo sempre a admin-panel
                httpForm.defaultSuccessUrl("/admin-panel", true);
            })

            .authorizeHttpRequests(registry -> {
                registry.requestMatchers("/css/**", "/js/**").permitAll();
                registry.anyRequest().authenticated();
            })
            .build();

    }
}
