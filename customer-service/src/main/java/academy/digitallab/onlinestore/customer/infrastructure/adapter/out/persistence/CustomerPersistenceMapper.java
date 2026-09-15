package academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence;

import academy.digitallab.onlinestore.customer.domain.model.Customer;
import academy.digitallab.onlinestore.customer.domain.model.Region;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.out.persistence.entity.RegionEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre entidades JPA y modelos de dominio.
 */
@Component
public class CustomerPersistenceMapper {

    public Region toDomain(RegionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Region(entity.getId(), entity.getName());
    }

    public RegionEntity toEntity(Region region) {
        if (region == null) {
            return null;
        }
        return new RegionEntity(region.id(), region.name());
    }

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Customer(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhotoUrl(),
                toDomain(entity.getRegion()),
                entity.getState());
    }

    public CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.id());
        entity.setFirstName(customer.firstName());
        entity.setLastName(customer.lastName());
        entity.setEmail(customer.email());
        entity.setPhotoUrl(customer.photoUrl());
        entity.setRegion(toEntity(customer.region()));
        entity.setState(customer.state());
        return entity;
    }
}
