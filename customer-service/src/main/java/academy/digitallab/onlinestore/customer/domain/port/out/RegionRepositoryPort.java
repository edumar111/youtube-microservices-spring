package academy.digitallab.onlinestore.customer.domain.port.out;

import academy.digitallab.onlinestore.customer.domain.model.Region;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: persistencia/consulta de regiones.
 */
public interface RegionRepositoryPort {

    List<Region> findAll();

    Optional<Region> findById(Long id);
}
