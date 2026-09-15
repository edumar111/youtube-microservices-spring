package academy.digitallab.onlinestore.customer.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTOs de cliente y región para la API REST.
 */
public final class CustomerDtos {

    private CustomerDtos() {
    }

    public record RegionResponse(Long id, String name) {
    }

    public record CustomerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank @Email String email,
            String photoUrl,
            @NotNull Long regionId) {
    }

    public record CustomerUpdateRequest(
            String firstName,
            String lastName,
            @Email String email,
            String photoUrl) {
    }

    public record CustomerResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String photoUrl,
            RegionResponse region,
            String state) {
    }
}
