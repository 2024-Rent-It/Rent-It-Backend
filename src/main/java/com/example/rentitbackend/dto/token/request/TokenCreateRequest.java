package com.example.rentitbackend.dto.token.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public record TokenCreateRequest(
        String nickname,
        String token
) {

}
