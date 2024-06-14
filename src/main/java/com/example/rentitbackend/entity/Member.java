package com.example.rentitbackend.entity;

import com.example.rentitbackend.common.MemberType;
import com.example.rentitbackend.dto.member.request.MemberUpdateRequest;
import com.example.rentitbackend.dto.sign_up.request.SignUpRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter //추가
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, scale = 20, unique = true)
    private String account;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private MemberType type;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE)
    private List<Product> sellingProducts;

    @OneToMany(mappedBy = "buyer")
    private List<Product> buyingProducts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Keyword> keywords;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Token> tokens;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<KeywordNotification> keywordNotifications;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PriceDropNotification> priceDropNotifications;




    public static Member from(SignUpRequest request, PasswordEncoder encoder) {	// 파라미터에 PasswordEncoder 추가
        return Member.builder()
                .account(request.account())
                .password(encoder.encode(request.password()))	// 수정
                .nickname(request.nickname())
                .email(request.email())
                .location(request.location())
                .type(MemberType.USER)
//                .createdAt(LocalDateTime.now())
                .build();
    }

    @Builder
    private Member(UUID id, String account, String password, String nickname, String email, String location, MemberType type) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.location = location;
        this.type = type;
    }

    public void update(MemberUpdateRequest newMember, PasswordEncoder encoder) {	// 파라미터에 PasswordEncoder 추가
        this.password = newMember.newPassword() == null || newMember.newPassword().isBlank()
                ? this.password : encoder.encode(newMember.newPassword());	// 수정
        this.nickname = newMember.nickname();
        this.email = newMember.email();
        this.location = newMember.location();
    }

    /** 비밀번호 변경 메서드 **/
    public void updatePassword(String password){
        this.password = password;
    }
}
