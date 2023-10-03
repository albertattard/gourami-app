package gourami.register;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Disabled("Need to provide authentication")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationControllerIT {

    @MockBean
    private RegistrationService service;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int randomServerPort;

    @Nested
    class RegisterTest {
        @Test
        void acceptRegistrationAndReturnTheRegistrationCode() {
            /* Given */
            final RegistrationRequest aValidRegistration = new RegistrationRequest("test|12345", "Albert", "Attard", "albertattard@email.com", "201 Main Street");
            final RegistrationResponse expectedResponse = RegistrationResponse.random();
            when(service.register(eq(aValidRegistration))).thenReturn(expectedResponse);

            /* When */
            final ResponseEntity<RegistrationResponse> result = postRegister(aValidRegistration);

            /* Then */
            assertThat(result).isNotNull();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody()).isEqualTo(expectedResponse);
        }

        private ResponseEntity<RegistrationResponse> postRegister(final RegistrationRequest registration) {
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
            ResponseEntity<RegistrationResponse> response = getRegistration(aValidRegistrationId);

            /* Then */
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().registrationId()).isEqualTo(aValidRegistrationId);
        }

        private ResponseEntity<RegistrationResponse> getRegistration(final UUID aValidRegistrationId) {
            return restTemplate.getForEntity(registrationLink(aValidRegistrationId), RegistrationResponse.class);
        }

        private URI registrationLink(final UUID id) {
            return URI.create(registrationBaseLink().concat("/%s".formatted(id)));
        }
    }

    private String registrationBaseLink() {
        return "http://localhost:%s/api/private/registration".formatted(randomServerPort);
    }
}
