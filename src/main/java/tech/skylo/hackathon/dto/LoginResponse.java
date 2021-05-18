package tech.skylo.hackathon.dto;

import lombok.Data;

@Data
public class LoginResponse {

    String userName;

    String access_token;

    String refresh_token;

    String token_type;

    Long expires_in;

}
