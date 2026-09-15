package academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.shopping.domain.model.Invoice;
import academy.digitallab.onlinestore.shopping.domain.port.in.InvoiceUseCase;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web.dto.InvoiceDtos.InvoiceRequest;
import academy.digitallab.onlinestore.shopping.infrastructure.adapter.in.web.dto.InvoiceDtos.InvoiceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceUseCase invoiceUseCase;
    private final WebMapper mapper;

    public InvoiceController(InvoiceUseCase invoiceUseCase, WebMapper mapper) {
        this.invoiceUseCase = invoiceUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<InvoiceResponse> listAll() {
        return invoiceUseCase.findAll().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable Long id) {
        return mapper.toResponse(invoiceUseCase.findById(id));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        Invoice created = invoiceUseCase.create(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @DeleteMapping("/{id}")
    public InvoiceResponse delete(@PathVariable Long id) {
        return mapper.toResponse(invoiceUseCase.delete(id));
    }
}
