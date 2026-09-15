package academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.customer.domain.model.Customer;
import academy.digitallab.onlinestore.customer.domain.model.Region;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.CustomerRequest;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.CustomerResponse;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.CustomerUpdateRequest;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.RegionResponse;
import org.springframework.stereotype.Component;

@Component
public class WebMapper {

    public RegionResponse toResponse(Region region) {
        if (region == null) {
            return null;
        }
        return new RegionResponse(region.id(), region.name());
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.id(),
                customer.firstName(),
                customer.lastName(),
                customer.email(),
                customer.photoUrl(),
                toResponse(customer.region()),
                customer.state());
    }

    public Customer toDomain(CustomerRequest request) {
        return new Customer(
                null,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.photoUrl(),
                new Region(request.regionId(), null),
                null);
    }

    public Customer toDomain(CustomerUpdateRequest request) {
        return new Customer(
                null,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.photoUrl(),
                null,
                null);
    }
}
