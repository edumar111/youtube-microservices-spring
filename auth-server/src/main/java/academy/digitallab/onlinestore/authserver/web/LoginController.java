package academy.digitallab.onlinestore.authserver.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sirve la página de login personalizada (estilo de la tienda). El POST /login lo procesa
 * Spring Security (UsernamePasswordAuthenticationFilter).
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
