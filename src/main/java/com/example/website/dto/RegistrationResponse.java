package com.example.website.dto;

public class RegistrationResponse {

    private Long registrationId;
    private String message;

    public RegistrationResponse() {}

    public RegistrationResponse(Long registrationId, String message) {
        this.registrationId = registrationId;
        this.message = message;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
