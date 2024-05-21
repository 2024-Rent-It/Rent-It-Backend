package com.example.rentitbackend.service;

import com.example.rentitbackend.dto.like.KeywordResponse;
import com.example.rentitbackend.dto.like.response.FavoriteResponse;
import com.example.rentitbackend.dto.product.response.ProductListResponse;
import com.example.rentitbackend.entity.*;
import com.example.rentitbackend.repository.KeywordRepository;
import com.example.rentitbackend.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
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

    public List<KeywordResponse> getKeywordsByMemberNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<Keyword> keywords = keywordRepository.findByMember(member);

        return keywords.stream()
                .map(keyword -> new KeywordResponse(keyword.getId(), keyword.getName(), keyword.getCreatedAt()))
                .collect(Collectors.toList());
    }

}
