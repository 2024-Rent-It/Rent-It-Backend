package com.example.rentitbackend.controller;

import com.example.rentitbackend.common.BasicResponse;
import com.example.rentitbackend.common.ErrorResponse;
import com.example.rentitbackend.common.Result;
import com.example.rentitbackend.dto.chatroom.ChatRoomDto;
import com.example.rentitbackend.dto.message.MessageDto;
import com.example.rentitbackend.service.ChatRoomService;
import com.example.rentitbackend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @MessageMapping("/chat/send")
    public void chat(MessageDto.Send message) {
//        messageService.sendMessage(message);
//        messagingTemplate.convertAndSend("/topic/chat/" + message.getReceiverId(), message);
        try {
            messageService.sendMessage(message);
            messagingTemplate.convertAndSend("/topic/chat/" + message.getReceiverId(), message);
        } catch (Exception e) {
            // 예외가 발생하면 처리하지 않음
            e.printStackTrace();
        }
    }

    @PostMapping("/chat/room")
    public ResponseEntity<BasicResponse> JoinChatRoom(@RequestBody ChatRoomDto.Request dto) {
        try {
            Long roomId = chatRoomService.joinChatRoom(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Result<>(roomId));
        } catch(IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "400"));
        }
    }

    @GetMapping("/chat/room")
    public ResponseEntity<BasicResponse> getChatRoomList(@RequestParam UUID memberId) {
        return ResponseEntity.ok(new Result<>(chatRoomService.getRoomList(memberId)));
    }

    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<BasicResponse> getChatRoomDetail(@PathVariable Long roomId) {
        return ResponseEntity.ok(new Result<>(chatRoomService.getRoomDetail(roomId)));
    }

    @GetMapping("/chat/room/{roomId}/last-message")
    public ResponseEntity<BasicResponse> getLastMessage(@PathVariable Long roomId) {
        MessageDto.Response lastMessage = chatRoomService.getLastMessage(roomId);
        return ResponseEntity.ok(new Result<>(lastMessage));
    }

    @GetMapping("/buyers/{productId}")
    public ResponseEntity<List<String>> getBuyerNicknames(@PathVariable Long productId) {
        List<String> buyerNicknames = chatRoomService.getBuyerNicknamesByProductId(productId);
        return ResponseEntity.ok(buyerNicknames);
    }

}
