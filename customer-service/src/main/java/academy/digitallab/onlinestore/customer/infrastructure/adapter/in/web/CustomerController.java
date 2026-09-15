package academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.customer.domain.model.Customer;
import academy.digitallab.onlinestore.customer.domain.port.in.CustomerUseCase;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.CustomerRequest;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.CustomerResponse;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.CustomerUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerUseCase customerUseCase;
    private final WebMapper mapper;

    public CustomerController(CustomerUseCase customerUseCase, WebMapper mapper) {
        this.customerUseCase = customerUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CustomerResponse> listAll(@RequestParam(required = false) Long regionId) {
        List<Customer> customers = (regionId != null)
                ? customerUseCase.findByRegion(regionId)
                : customerUseCase.findAll();
        return customers.stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return mapper.toResponse(customerUseCase.findById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        Customer created = customerUseCase.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        return mapper.toResponse(customerUseCase.update(id, mapper.toDomain(request)));
    }

    @DeleteMapping("/{id}")
    public CustomerResponse delete(@PathVariable Long id) {
        return mapper.toResponse(customerUseCase.delete(id));
    }
}
