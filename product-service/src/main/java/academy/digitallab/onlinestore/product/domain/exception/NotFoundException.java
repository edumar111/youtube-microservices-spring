package academy.digitallab.onlinestore.product.domain.exception;

/**
 * Se lanza cuando un recurso del dominio (producto o categoría) no existe.
 * La capa de infraestructura la traduce a HTTP 404.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
