package gourami.register;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegisterController {

    @PostMapping("/")
    public ResponseEntity<RegistrationResponse> register(final RegistrationRequest request) {
        return ResponseEntity.ok(RegistrationResponse.random());
    }
}
