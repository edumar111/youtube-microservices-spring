package academy.digitallab.onlinestore.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Authorization Server del curso (ep. 11). Emite tokens JWT (OAuth2) para el acceso a los
 * microservicios. Sustituye a Keycloak: se usa Spring Authorization Server.
 */
@SpringBootApplication
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
