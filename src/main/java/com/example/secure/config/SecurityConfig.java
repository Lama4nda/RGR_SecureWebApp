package com.example.secure.config;

import com.example.secure.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.example.secure.config.Base64PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            // Дозволяємо H2-консоль та інші фрейми
            .headers().frameOptions().disable()
            .and()
            
            .authorizeHttpRequests(auth -> auth
                // 1. Доступ для всіх
                .antMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                // 2. ТІЛЬКИ АДМІН: Додавання та видалення
                .antMatchers("/add**", "/delete**", "/edit**", "/update**").hasAuthority("ADMIN")
                
                // 3. Панель управління (/dashboard) доступна всім авторизованим
                .antMatchers("/dashboard").authenticated()
                
                // Всі інші запити теж вимагають авторизації
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/dashboard", true) // Завжди на dashboard після входу
            )
            .logout(logout -> logout
                .permitAll()
                .logoutSuccessUrl("/") // Після виходу - на головну
            );
        
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Base64PasswordEncoder();
    }
}