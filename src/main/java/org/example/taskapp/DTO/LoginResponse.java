package org.example.taskapp.DTO;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String accessToken;
    private long accessTokenExpiration;
    private String refreshToken;
    private String username;
    private String message;

    public LoginResponse(boolean success, String accessToken, long accessTokenExpiration,
                         String refreshToken, String username, String message) {
        this.success = success;
        this.accessToken = accessToken;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshToken = refreshToken;
        this.username = username;
        this.message = message;
    }
}
