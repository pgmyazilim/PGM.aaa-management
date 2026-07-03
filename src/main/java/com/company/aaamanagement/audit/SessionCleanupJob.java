package com.company.aaamanagement.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupJob {

    private final SessionRepository sessionRepository;

    @Scheduled(fixedDelayString = "${aaa.session-cleanup.delay:PT5M}")
    @Transactional
    public void closeExpiredSessions() {
        int closed = sessionRepository.closeExpiredSessions(LocalDateTime.now(ZoneOffset.UTC));
        if (closed > 0) {
            log.info("Süresi dolmuş {} açık oturum kapatıldı.", closed);
        }
    }
}
