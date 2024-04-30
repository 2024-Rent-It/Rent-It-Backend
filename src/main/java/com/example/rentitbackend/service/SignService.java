package com.example.rentitbackend.service;

import com.example.rentitbackend.dto.sign_in.request.SignInRequest;
import com.example.rentitbackend.dto.sign_in.response.SignInResponse;
import com.example.rentitbackend.dto.sign_up.request.SignUpRequest;
import com.example.rentitbackend.dto.sign_up.response.SignUpResponse;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.repository.MemberRepository;
import com.example.rentitbackend.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SignService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public SignUpResponse registerMember(SignUpRequest request) {
        if (!request.password().equals(request.password2())) {
            throw new IllegalArgumentException("2개의 패스워드가 일치하지 않습니다.");
        }
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.findByNickname(request.nickname()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = memberRepository.save(Member.from(request, encoder));

        try {
            memberRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        } catch (Exception e){
            throw new IllegalArgumentException("회원가입 오류입니다.");
        }


        return SignUpResponse.from(member);
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request) {
        Member member = memberRepository.findByAccount(request.account())
                .filter(it -> encoder.matches(request.password(), it.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        String token = tokenProvider.createToken(String.format("%s:%s", member.getId(), member.getType()));
        return new SignInResponse(member.getNickname(), member.getEmail(), member.getLocation(),member.getType(),token);
    }
}
