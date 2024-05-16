package com.example.rentitbackend.service;

import com.example.rentitbackend.entity.Keyword;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.repository.KeywordRepository;
import com.example.rentitbackend.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

public class KeywordService {
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;

    @Autowired
    public KeywordService(MemberRepository memberRepository, KeywordRepository keywordRepository) {
        this.memberRepository = memberRepository;
        this.keywordRepository = keywordRepository;
    }

    @Transactional
    public Keyword addKeyword(UUID memberId, String name) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + memberId));
        Keyword keyword = new Keyword();
        keyword.setMember(member);
        keyword.setName(name);
        keyword.setCreatedAt(LocalDateTime.now());
        return keywordRepository.save(keyword);
    }

    @Transactional
    public void deleteKeyword(Long keywordId) {
        keywordRepository.deleteById(keywordId);
    }
}
