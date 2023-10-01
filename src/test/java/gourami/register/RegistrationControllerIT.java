package gourami.register;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int randomServerPort;

    @Nested
    class RegisterTest {
        @Test
        void acceptRegistrationAndReturnTheRegistrationCode() {
            /* Given */
            final RegistrationRequest aValidRegistration = new RegistrationRequest("Albert", "Attard", "albertattard@email.com");

            /* When */
            final ResponseEntity<RegistrationResponse> response = invokeRegister(aValidRegistration);

            /* Then */
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            /* TODO: mock service and assert response */
            assertThat(response.getBody().registrationId()).isNotNull();

            System.out.println(response);
        }

        private ResponseEntity<RegistrationResponse> invokeRegister(final RegistrationRequest registration) {
            return restTemplate.postForEntity(registerLink(), createRequest(registration), RegistrationResponse.class);
        }

        private static HttpEntity<RegistrationRequest> createRequest(final RegistrationRequest registration) {
            final HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            return new HttpEntity<>(registration, headers);
        }

        private URI registerLink() {
            return URI.create(registrationBaseLink());
        }
    }

    @Nested
    class Registration {
        @Test
        void returnTheRegistrationWithTheGivenId() {
            /* Given */
            final UUID aValidRegistrationId = UUID.randomUUID();

            /* When */
            ResponseEntity<RegistrationResponse> response = invokeRegistration(aValidRegistrationId);

            /* Then */
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().registrationId()).isEqualTo(aValidRegistrationId);
        }

        private ResponseEntity<RegistrationResponse> invokeRegistration(final UUID aValidRegistrationId) {
            return restTemplate.getForEntity(registrationLink(aValidRegistrationId), RegistrationResponse.class);
        }

        private URI registrationLink(final UUID id) {
            return URI.create(registrationBaseLink().concat("/%s".formatted(id)));
        }
    }

    private String registrationBaseLink() {
        return "http://localhost:%s/registration".formatted(randomServerPort);
    }
}
