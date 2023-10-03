package gourami.register;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/private/registration")
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(final RegistrationService service) {
        this.service = requireNonNull(service);
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@AuthenticationPrincipal final Jwt principal, @RequestBody final RegistrationRequest request) {
        final RegistrationResponse response = service.register(request);
        return ResponseEntity
                .created(location(response))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponse> registration(final @PathVariable("id") UUID id) {
        return ResponseEntity
                .ok(new RegistrationResponse(id));
    }

    private static URI location(final RegistrationResponse response) {
        return URI.create("/registration/%s".formatted(response.registrationId()));
    }
}
