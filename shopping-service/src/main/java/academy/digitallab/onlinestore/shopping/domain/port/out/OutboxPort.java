package academy.digitallab.onlinestore.shopping.domain.port.out;

/**
 * Puerto de salida del patrón Outbox (ep. 12): registra un evento en la misma transacción
 * local que la escritura de negocio, garantizando atomicidad. Un relay lo publica luego a Kafka.
 */
public interface OutboxPort {

    void append(String aggregateType, Long aggregateId, String eventType, Object payload);
}
