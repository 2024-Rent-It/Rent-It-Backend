package com.example.rentitbackend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailVo {
    private String toAddress;
    private String title;
    private String message;
    private String fromAddress;
}