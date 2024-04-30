package com.example.rentitbackend.service;

import com.example.rentitbackend.entity.MailVo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class MailService {

    private final JavaMailSender mailSender;

    private static final String title = "Rent-It 임시 비밀번호 안내 이메일입니다.";
    private static final String message = "안녕하세요. Rent-It 임시 비밀번호 안내 메일입니다. "
            +"\n" + "회원님의 임시 비밀번호는 아래와 같습니다. 로그인 후 반드시 비밀번호를 변경해주세요."+"\n";
    private static final String fromAddress = "lentit2024@gmail.com";

    /** 이메일 생성 **/
    public MailVo createMail(String tmpPassword, String memberEmail) {

        MailVo mailVo = MailVo.builder()
                .toAddress(memberEmail)
                .title(title)
                .message(message + tmpPassword)
                .fromAddress(fromAddress)
                .build();

        log.info("메일 생성 완료");
        return mailVo;
    }

    /** 이메일 전송 **/
    public void sendMail(MailVo mailVo) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailVo.getToAddress());
        mailMessage.setSubject(mailVo.getTitle());
        mailMessage.setText(mailVo.getMessage());
        mailMessage.setFrom(mailVo.getFromAddress());
        mailMessage.setReplyTo(mailVo.getFromAddress());

        mailSender.send(mailMessage);

        log.info("메일 전송 완료");
    }
}
