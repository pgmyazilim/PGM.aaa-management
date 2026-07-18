package com.company.aaamanagement.audit;

import com.company.aaamanagement.action.ActionRepository;
import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void listActionLogs_passesAllFiltersAndSortsByOccurredAtDesc() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);
        when(actionLogRepository.findByFilters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        service.listActionLogs(3, 7, true, "kilit", from, to, 0, 50);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(actionLogRepository).findByFilters(eq(3), eq(7), eq(true), eq("kilit"), eq(from), eq(to),
                pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "occurredAtUtc"));
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    @Test
    void listRecordAudits_passesAllFiltersAndSortsByOccurredAtDesc() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);
        when(recordAuditRepository.findByFilters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        service.listRecordAudits(2, 9, OperationType.U, "hata", from, to, 0, 50);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(recordAuditRepository).findByFilters(eq(2), eq(9), eq(OperationType.U), eq("hata"), eq(from), eq(to),
                pageableCaptor.capture());
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
}
