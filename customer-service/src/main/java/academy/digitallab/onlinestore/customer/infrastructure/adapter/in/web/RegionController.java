package academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web;

import academy.digitallab.onlinestore.customer.domain.port.in.RegionUseCase;
import academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto.CustomerDtos.RegionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {

    private final RegionUseCase regionUseCase;
    private final WebMapper mapper;

    public RegionController(RegionUseCase regionUseCase, WebMapper mapper) {
        this.regionUseCase = regionUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<RegionResponse> listAll() {
        return regionUseCase.findAll().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public RegionResponse getById(@PathVariable Long id) {
        return mapper.toResponse(regionUseCase.findById(id));
    }
}
