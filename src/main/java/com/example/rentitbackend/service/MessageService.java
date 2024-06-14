package com.example.rentitbackend.service;

import com.example.rentitbackend.dto.message.MessageDto;
import com.example.rentitbackend.repository.MessageRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    //    @Transactional(rollbackFor = Exception.class)
//    public void sendMessage(MessageDto.Send message) {
//        messageRepository.save(message.toMessage());
//    }

//    @Transactional(rollbackFor = Exception.class)
    @Transactional
    public void sendMessage(MessageDto.Send message) throws Exception {
        try {
            messageRepository.save(message.toMessage());
        } catch (Exception e) {
            // 예외가 발생하면 롤백하고 예외를 다시 던짐
            throw new Exception("메시지를 저장하는 동안 오류가 발생했습니다.", e);
        }
    }

}