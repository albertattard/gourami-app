package gourami.register;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    public RegistrationResponse register(final RegistrationRequest request) {
        return RegistrationResponse.random();
    }
}
