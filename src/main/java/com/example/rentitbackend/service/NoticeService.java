package com.example.rentitbackend.service;
import com.example.rentitbackend.entity.Notice;
import com.example.rentitbackend.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }

    public Notice createNotice(Notice notice) {
        notice.setCreatedAt(LocalDateTime.now());
        notice.setUpdatedAt(LocalDateTime.now());
        return noticeRepository.save(notice);
    }

    public Notice updateNotice(Long id, Notice updatedNotice) {
        return noticeRepository.findById(id)
                .map(notice -> {
                    notice.setTitle(updatedNotice.getTitle());
                    notice.setContent(updatedNotice.getContent());
                    notice.setUpdatedAt(LocalDateTime.now());
                    return noticeRepository.save(notice);
                })
                .orElse(null);
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    public void increaseViews(Long id) {
        noticeRepository.findById(id).ifPresent(notice -> {
            notice.setViews(notice.getViews() + 1);
            noticeRepository.save(notice);
        });
    }
}
