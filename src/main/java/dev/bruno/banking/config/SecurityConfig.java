package dev.bruno.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/transactions/**").permitAll() // Libera os endpoints de transações
                        .requestMatchers("/users/**").permitAll() // Libera os endpoints de usuários
                        .anyRequest().permitAll() // Permite qualquer outro endpoint
                )
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para simplificar testes
                .httpBasic(AbstractHttpConfigurer::disable) // Substituímos o httpBasic() por uma abordagem mais moderna
                .formLogin(AbstractHttpConfigurer::disable); // Também desativa o formLogin, se não necessário

        return http.build();
    }
}
