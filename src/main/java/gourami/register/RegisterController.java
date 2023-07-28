package gourami.register;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Qualifier("/register")
public class RegisterController {

    @PostMapping("/")
    public ResponseEntity<RegistrationResponse> register(final RegistrationRequest request) {
        return ResponseEntity.ok(RegistrationResponse.random());
    }
}
