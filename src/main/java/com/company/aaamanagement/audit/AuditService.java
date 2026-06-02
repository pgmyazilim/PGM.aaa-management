package com.company.aaamanagement.audit;

import com.company.aaamanagement.action.ActionRepository;
import com.company.aaamanagement.domain.ActionLog;
import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.RecordAudit;
import com.company.aaamanagement.domain.Session;
import com.company.aaamanagement.domain.TrackedTable;
import com.company.aaamanagement.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ActionLogRepository actionLogRepository;
    private final SessionRepository sessionRepository;
    private final RecordAuditRepository recordAuditRepository;
    private final TrackedTableRepository trackedTableRepository;
    private final ActionRepository actionRepository;
    private final UserRepository userRepository;

    public Page<ActionLog> listActionLogs(Integer actionId, Boolean success,
                                           LocalDateTime from, LocalDateTime to,
                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAtUtc"));
        return actionLogRepository.findByFilters(actionId, success, from, to, pageable);
    }

    public Page<Session> listSessions(Integer userId, Boolean open,
                                       LocalDateTime from, LocalDateTime to,
                                       int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "openedAtUtc"));
        return sessionRepository.findByFilters(userId, open, from, to, pageable);
    }

    public Page<RecordAudit> listRecordAudits(Integer tableId, OperationType opType,
                                               LocalDateTime from, LocalDateTime to,
                                               int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAtUtc"));
        return recordAuditRepository.findByFilters(tableId, opType, from, to, pageable);
    }

    public List<TrackedTable> getAllTrackedTables() {
        return trackedTableRepository.findAllByOrderByNameAsc();
    }

    public OperationType[] getOperationTypes() {
        return OperationType.values();
    }
}
