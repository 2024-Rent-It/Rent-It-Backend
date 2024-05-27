package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.token.TokenDTO;
import com.example.rentitbackend.dto.token.request.TokenCreateRequest;
import com.example.rentitbackend.entity.Token;
import com.example.rentitbackend.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<Token> registerToken(@RequestBody TokenCreateRequest request) {
        Token registeredToken = tokenService.registerToken(request.nickname(), request.token());
        return ResponseEntity.ok(registeredToken);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteToken(@RequestParam String nickname) {
        tokenService.deleteTokenByMemberNickname(nickname);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findByNickname")
    public ResponseEntity<TokenDTO> findByMemberNickname(@RequestParam String nickname) {
        TokenDTO tokenDTO = tokenService.findTokenByMemberNickname(nickname);
        return ResponseEntity.ok(tokenDTO);
    }
}
