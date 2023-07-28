package gourami.register;

import java.util.UUID;

public record RegistrationResponse(UUID registrationId) {
    public static RegistrationResponse random() {
        return new RegistrationResponse(UUID.randomUUID());
    }
}
