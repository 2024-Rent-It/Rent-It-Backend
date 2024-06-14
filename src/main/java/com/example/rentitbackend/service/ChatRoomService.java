package com.example.rentitbackend.service;
import com.example.rentitbackend.dto.chatroom.ChatRoomDto;
import com.example.rentitbackend.dto.message.MessageDto;
import com.example.rentitbackend.entity.ChatRoom;
import com.example.rentitbackend.entity.Message;
import com.example.rentitbackend.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    @Transactional(rollbackFor = Exception.class)
    public Long joinChatRoom(ChatRoomDto.Request dto) throws IllegalStateException {
        if(dto.getBuyerId().equals(dto.getSellerId())) {
            throw new IllegalStateException("자신과의 채팅방은 만들 수 없습니다.");
        }
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByMemberAndProduct(dto.getBuyerId(), dto.getSellerId(), dto.getProductId());
        if(chatRoom.isPresent()) {
            return chatRoom.get().getId();
        } else {
            return chatRoomRepository.save(dto.toChatRoom());
        }
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDto.Response> getRoomList(UUID memberId) {
        return chatRoomRepository.findListByMemberId(memberId).stream().map(ChatRoomDto.Response::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoomDto.Detail getRoomDetail(Long roomId) {
        Optional<ChatRoomDto.Detail> room = chatRoomRepository.findById(roomId).map(ChatRoomDto.Detail::of);
        return room.orElseThrow();
    }

    @Transactional(readOnly = true)
    public MessageDto.Response getLastMessage(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        List<Message> messages = chatRoom.getMessageList();
        if (messages.isEmpty()) {
            return null;
        }
        Message lastMessage = messages.get(messages.size() - 1);
        return MessageDto.Response.of(lastMessage);
    }

    public List<String> getBuyerNicknamesByProductId(Long productId) {
        return chatRoomRepository.findDistinctBuyerNicknamesByProductId(productId);
    }

}