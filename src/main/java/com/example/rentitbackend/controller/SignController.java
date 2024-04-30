package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.ApiResponse;
import com.example.rentitbackend.dto.sign_in.request.SignInRequest;
import com.example.rentitbackend.dto.sign_up.request.SignUpRequest;
import com.example.rentitbackend.entity.MailVo;
import com.example.rentitbackend.service.MailService;
import com.example.rentitbackend.service.MemberService;
import com.example.rentitbackend.service.SignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 가입 및 로그인")
@RequiredArgsConstructor
@RestController
@RequestMapping
public class SignController {
    private final SignService signService;
    private final MemberService memberService;

    private final MailService mailService;


    @Operation(summary = "회원 가입")
    @PostMapping("/sign-up")
    public ApiResponse signUp(@RequestBody SignUpRequest request) {
        return ApiResponse.success(signService.registerMember(request));
    }

    @Operation(summary = "로그인")
    @PostMapping("/sign-in")
    public ApiResponse signIn(@RequestBody SignInRequest request) {
        return ApiResponse.success(signService.signIn(request));
    }

    @Operation(summary = "이메일 DB 존재 유무 조회")
    @GetMapping("/checkEmail")
    public boolean checkEmail(@RequestParam("memberEmail") String memberEmail){
        return memberService.checkEmail(memberEmail);
    }

    @Operation(summary = "임시 비밀번호 발급")
    @PostMapping("/sendPwd")
    public ApiResponse sendPwdEmail(@RequestParam("memberEmail") String memberEmail) {

        /** 임시 비밀번호 생성 **/
        String tmpPassword = memberService.getTmpPassword();

        /** 임시 비밀번호 저장 **/
        memberService.updateTmpPassword(tmpPassword, memberEmail);

        /** 메일 생성 & 전송 **/
        MailVo mail = mailService.createMail(tmpPassword, memberEmail);
        mailService.sendMail(mail);
//        log.info("임시 비밀번호 전송 완료");

        return ApiResponse.success("임시 비밀번호가 성공적으로 발급되었습니다.");
    }

    @GetMapping("/accounts/{account}")
    public ResponseEntity<String> checkAccountAvailability(@PathVariable String account) {
        boolean isAvailable = memberService.checkAccount(account);
        return ResponseEntity.status(HttpStatus.OK).body(isAvailable ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다." );
    }

    @GetMapping("/nicknames/{nickname}")
    public ResponseEntity<String> checkNicknameAvailability(@PathVariable String nickname) {
        boolean isAvailable = memberService.checkNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(isAvailable ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다." );
    }

    @GetMapping("/emails/{email}")
    public ResponseEntity<String> checkEmailAvailability(@PathVariable String email) {
        boolean isAvailable = memberService.checkEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(isAvailable ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다." );
    }


}
