package gourami.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import static java.util.Objects.requireNonNull;

class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String audience;

    public static AudienceValidator of(final String audience) {
        requireNonNull(audience);
        return new AudienceValidator(audience);
    }

    private AudienceValidator(final String audience) {
        this.audience = audience;
    }

    public OAuth2TokenValidatorResult validate(final Jwt jwt) {
        return jwt.getAudience().contains(audience)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "The required audience is missing", null));
    }
}
