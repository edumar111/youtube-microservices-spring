package academy.digitallab.onlinestore.product.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Ep. 11 — product-service como OAuth2 Resource Server (valida JWT por JWKS).
 *
 * <p>Como en una tienda online real, el <b>catálogo es público</b>: cualquier visitante puede
 * navegar y buscar sin sesión (GET de productos y categorías). Las operaciones de escritura y el
 * ajuste de stock exigen autenticación. El login solo se pide al pagar (en el frontend).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // El ajuste de stock (aunque sea GET) requiere autenticación.
                        .requestMatchers(HttpMethod.GET, "/products/*/stock").authenticated()
                        // Catálogo público: navegar y buscar sin sesión.
                        .requestMatchers(HttpMethod.GET, "/products", "/products/**",
                                "/categories", "/categories/**").permitAll()
                        // Escrituras (crear/actualizar/borrar) requieren autenticación.
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
