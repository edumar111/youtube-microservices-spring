package academy.digitallab.onlinestore.customer.domain.model;

/**
 * Modelo de dominio de Cliente. Inmutable (record), sin JPA ni Spring.
 */
public record Customer(
        Long id,
        String firstName,
        String lastName,
        String email,
        String photoUrl,
        Region region,
        String state
) {

    public Customer withId(Long newId) {
        return new Customer(newId, firstName, lastName, email, photoUrl, region, state);
    }

    public Customer withState(String newState) {
        return new Customer(id, firstName, lastName, email, photoUrl, region, newState);
    }
}
