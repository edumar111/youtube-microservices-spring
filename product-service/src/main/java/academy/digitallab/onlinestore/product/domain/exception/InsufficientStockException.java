package academy.digitallab.onlinestore.product.domain.exception;

/**
 * Regla de negocio: no se puede descontar más stock del disponible.
 * La capa de infraestructura la traduce a HTTP 409 (Conflict).
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
