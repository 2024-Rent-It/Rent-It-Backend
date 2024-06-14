package com.example.rentitbackend.dto.message;
import com.example.rentitbackend.dto.member.MemberDto;
import com.example.rentitbackend.entity.ChatRoom;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDto {
    @Getter
    public static class Send {
        private String message;
        private UUID senderId;
        private UUID receiverId;
        private Long roomId;

        public Message toMessage() {
            return Message.builder()
                    .message(message)
                    .sender(Member.builder().id(senderId).build())
                    .chatRoom(ChatRoom.builder().id(roomId).build())
                    .receiver(Member.builder().id(receiverId).build())
                    .sendTime(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;
        private MemberDto.Response sender;
        private LocalDateTime sendTime;

        public static Response of(Message message) {
            return Response.builder()
                    .message(message.getMessage())
                    .sender(MemberDto.Response.of(message.getSender()))
                    .sendTime(message.getSendTime())
                    .build();
        }
    }
}
