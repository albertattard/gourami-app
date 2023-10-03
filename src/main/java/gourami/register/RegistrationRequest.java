package gourami.register;

public record RegistrationRequest(String userId, String name, String surname, String emailAddress, String postalAddress) {}
