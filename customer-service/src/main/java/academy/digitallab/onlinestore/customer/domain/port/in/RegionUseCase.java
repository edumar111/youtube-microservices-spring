package academy.digitallab.onlinestore.customer.domain.port.in;

import academy.digitallab.onlinestore.customer.domain.model.Region;

import java.util.List;

/**
 * Puerto de entrada: consulta de regiones (dato de referencia).
 */
public interface RegionUseCase {

    List<Region> findAll();

    Region findById(Long id);
}
