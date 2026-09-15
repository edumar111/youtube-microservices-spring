package academy.digitallab.onlinestore.customer.domain.exception;

/**
 * Se lanza cuando un cliente o región no existe. Se traduce a HTTP 404.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
