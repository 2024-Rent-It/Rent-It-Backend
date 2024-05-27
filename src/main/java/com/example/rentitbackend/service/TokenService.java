package com.example.rentitbackend.service;

import com.example.rentitbackend.dto.token.TokenDTO;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Token;
import com.example.rentitbackend.repository.MemberRepository;
import com.example.rentitbackend.repository.TokenRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    public TokenService(TokenRepository tokenRepository, MemberRepository memberRepository) {
        this.tokenRepository = tokenRepository;
        this.memberRepository = memberRepository;
    }

    public Token registerToken(String nickname, String tokenString) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + nickname));

        // 중복 체크
        if (tokenRepository.findByTokenAndMember(tokenString, member).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 토큰입니다: " + tokenString);
        }

        Token token = new Token();
        token.setToken(tokenString);
        token.setMember(member);


        return tokenRepository.save(token);
    }

    public void deleteTokenByMemberNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + nickname));

        tokenRepository.deleteByMember(member);
    }

    public TokenDTO findTokenByMemberNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + nickname));

        Token token = tokenRepository.findByMember(member)
                .orElseThrow(() -> new EntityNotFoundException("Token not found for member with nickname: " + nickname));

        return new TokenDTO(token.getId(), token.getToken());
    }
}
