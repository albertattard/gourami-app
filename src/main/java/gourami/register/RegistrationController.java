package gourami.register;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(final RegistrationRequest request) {
        final RegistrationResponse response = RegistrationResponse.random();
        return ResponseEntity
                .created(location(response))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponse> registration(final @PathVariable("id") UUID id) {
        final RegistrationResponse response = new RegistrationResponse(id);
        return ResponseEntity
                .ok(response);
    }

    private static URI location(final RegistrationResponse response) {
        return URI.create("/registration/%s".formatted(response.registrationId()));
    }
}
