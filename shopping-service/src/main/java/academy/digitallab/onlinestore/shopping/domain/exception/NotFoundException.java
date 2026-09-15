package academy.digitallab.onlinestore.shopping.domain.exception;

/**
 * Se lanza cuando una factura no existe. Se traduce a HTTP 404.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
