package com.company.aaamanagement.audit;

import com.company.aaamanagement.action.ActionRepository;
import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.ActionLog;
import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.RecordAudit;
import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock ActionLogRepository actionLogRepository;
    @Mock SessionRepository sessionRepository;
    @Mock RecordAuditRepository recordAuditRepository;
    @Mock TrackedTableRepository trackedTableRepository;
    @Mock ActionRepository actionRepository;
    @Mock UserRepository userRepository;
    @InjectMocks AuditService service;

    @Test
    void listActionLogs_delegatesToSpecificationAndSortsByOccurredAtDesc() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);
        when(actionLogRepository.findAll(ArgumentMatchers.<Specification<ActionLog>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.listActionLogs(3, 7, true, from, to, 0, 50);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(actionLogRepository).findAll(ArgumentMatchers.<Specification<ActionLog>>any(), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "occurredAtUtc"));
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    @Test
    void listRecordAudits_delegatesToSpecificationAndSortsByOccurredAtDesc() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);
        when(recordAuditRepository.findAll(ArgumentMatchers.<Specification<RecordAudit>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.listRecordAudits(2, 9, OperationType.U, from, to, 0, 50);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(recordAuditRepository).findAll(ArgumentMatchers.<Specification<RecordAudit>>any(), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "occurredAtUtc"));
    }

    @Test
    void getAllActions_delegatesToRepositoryOrderedByName() {
        List<Action> actions = List.of(Action.builder().actionId(1).name("Giriş").build());
        when(actionRepository.findAllByOrderByNameAsc()).thenReturn(actions);

        assertThat(service.getAllActions()).isSameAs(actions);
    }

    @Test
    void getAllUsersForFilter_sortsByLastNameThenFirstName() {
        List<User> users = List.of(User.builder().userId(1).firstName("Ada").lastName("Lovelace").build());
        when(userRepository.findAll(Sort.by("lastName", "firstName"))).thenReturn(users);

        assertThat(service.getAllUsersForFilter()).isSameAs(users);
    }

    @Test
    void findRecordAuditById_whenFound_returnsIt() {
        RecordAudit audit = RecordAudit.builder().recordAuditId(5L).build();
        when(recordAuditRepository.findById(5L)).thenReturn(Optional.of(audit));

        assertThat(service.findRecordAuditById(5L)).isSameAs(audit);
    }

    @Test
    void findRecordAuditById_whenMissing_throwsEntityNotFound() {
        when(recordAuditRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findRecordAuditById(9L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
