package academy.digitallab.onlinestore.customer.domain.service;

import academy.digitallab.onlinestore.customer.domain.exception.NotFoundException;
import academy.digitallab.onlinestore.customer.domain.model.Region;
import academy.digitallab.onlinestore.customer.domain.port.in.RegionUseCase;
import academy.digitallab.onlinestore.customer.domain.port.out.RegionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de región.
 */
@Service
public class RegionService implements RegionUseCase {

    private final RegionRepositoryPort regionRepository;

    public RegionService(RegionRepositoryPort regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Region> findAll() {
        return regionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Region findById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Region not found: " + id));
    }
}
