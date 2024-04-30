package com.example.rentitbackend.service;

import com.example.rentitbackend.dto.member.request.MemberUpdateRequest;
import com.example.rentitbackend.dto.member.response.MemberDeleteResponse;
import com.example.rentitbackend.dto.member.response.MemberInfoResponse;
import com.example.rentitbackend.dto.member.response.MemberUpdateResponse;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;	// 추가

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(UUID id) {
        return memberRepository.findById(id)
                .map(MemberInfoResponse::from)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public MemberDeleteResponse deleteMember(UUID id) {
        if (!memberRepository.existsById(id)) return new MemberDeleteResponse(false);
        memberRepository.deleteById(id);
        return new MemberDeleteResponse(true);
    }

    @Transactional
    public MemberUpdateResponse updateMember(UUID id, MemberUpdateRequest request) {
        return memberRepository.findById(id)
                .filter(member -> encoder.matches(request.password(), member.getPassword()))	// 암호화된 비밀번호와 비교하도록 수정
                .map(member -> {
                    member.update(request, encoder);	// 새 비밀번호를 암호화하도록 수정
                    return MemberUpdateResponse.of(true, member);
                })
                .orElseThrow(() -> new NoSuchElementException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    @Transactional
    public MemberUpdateResponse updateNickname(UUID id, String nickname) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
        member.setNickname(nickname);
        return MemberUpdateResponse.of(true, member);
    }

    @Transactional
    public MemberUpdateResponse updateEmail(UUID id, String email) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
        member.setEmail(email);
        return MemberUpdateResponse.of(true, member);
    }

    @Transactional
    public MemberUpdateResponse updateLocation(UUID id, String location) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
        member.setLocation(location);
        return MemberUpdateResponse.of(true, member);
    }

//    @Transactional
//    public MemberUpdateResponse updatePassword(UUID id, String currentPassword, String newPassword) {
//        Member member = memberRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
//        if (!encoder.matches(currentPassword, member.getPassword())) {
//            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
//        }
//        member.setPassword(encoder.encode(newPassword));
//        return MemberUpdateResponse.of(true, member);
//    }

    @Transactional
    public MemberUpdateResponse checkCurrentPassword(UUID id, String currentPassword) {
        Member member = memberRepository.findById(id)
              .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
        if (!encoder.matches(currentPassword, member.getPassword())) {
         throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        return MemberUpdateResponse.of(true, member);
}

    @Transactional
    public MemberUpdateResponse updatePassword(UUID id, String newPassword) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
        member.setPassword(encoder.encode(newPassword));
        return MemberUpdateResponse.of(true, member);
    }




    /** 이메일이 존재하는지 확인 **/
    public boolean checkEmail(String memberEmail) {
        /* 이메일이 존재하면 true, 이메일이 없으면 false  */
        return memberRepository.existsByEmail(memberEmail);
    }


    /** 아이디가 존재하는지 확인 **/
    public boolean checkAccount(String account) {
        /* 아이디가 존재하면 true, 아이디가 없으면 false */
        return memberRepository.existsByAccount(account);
//        return memberRepository.findByAccount(account).isPresent();
    }

    /** 닉네임이 존재하는지 확인 **/
    public boolean checkNickname(String nickname) {
        /* 닉네임이 존재하면 true, 닉네임이 없으면 false */
        return memberRepository.existsByNickname(nickname);
    }

    public String getTmpPassword() {
        char[] charSet = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String pwd = "";

        /* 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 조합 */
        int idx = 0;
        for(int i = 0; i < 10; i++){
            idx = (int) (charSet.length * Math.random());
            pwd += charSet[idx];
        }

//        log.info("임시 비밀번호 생성");

        return pwd;
    }

    /** 임시 비밀번호 업데이트 **/
    public void updateTmpPassword(String tmpPassword, String memberEmail) {

        String encryptPassword = encoder.encode(tmpPassword);
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(() ->
                new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        member.updatePassword(encryptPassword);
//        log.info("임시 비밀번호 업데이트");
    }

    public Member findMemberById(UUID id) {
        return memberRepository.findById(id)
                .orElse(null); // 찾지 못할 경우 null 반환 혹은 예외처리를 하세요.
    }

    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElse(null); // 찾지 못할 경우 null 반환 혹은 예외처리를 하세요.
    }
}
