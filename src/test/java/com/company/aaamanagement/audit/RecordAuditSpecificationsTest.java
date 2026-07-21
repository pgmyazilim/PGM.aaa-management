package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.RecordAudit;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "rawtypes"})
class RecordAuditSpecificationsTest {

    private final Root<RecordAudit> root = mock(Root.class);
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final CriteriaBuilder cb = mock(CriteriaBuilder.class);

    @Test
    void filters_withNoCriteria_addsNoPredicatesAndTouchesNoColumn() {
        when(cb.and(any(Predicate[].class))).thenAnswer(inv -> mock(Predicate.class));

        RecordAuditSpecifications.filters(null, null, null, null, null).toPredicate(root, query, cb);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        assertThat(captor.getValue()).isEmpty();
        verifyNoInteractions(root);
    }

    @Test
    void filters_withAllCriteria_addsExactlyOnePredicatePerFilter() {
        Path tablePath = mock(Path.class);
        Path tableIdPath = mock(Path.class);
        when(root.get("trackedTable")).thenReturn(tablePath);
        when(tablePath.get("trackedTableId")).thenReturn(tableIdPath);

        Path actorUserPath = mock(Path.class);
        Path userIdPath = mock(Path.class);
        when(root.get("actorUser")).thenReturn(actorUserPath);
        when(actorUserPath.get("userId")).thenReturn(userIdPath);

        Path opTypePath = mock(Path.class);
        when(root.get("operationType")).thenReturn(opTypePath);

        Path occurredAtPath = mock(Path.class);
        when(root.get("occurredAtUtc")).thenReturn(occurredAtPath);

        when(cb.and(any(Predicate[].class))).thenAnswer(inv -> mock(Predicate.class));

        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);

        RecordAuditSpecifications.filters(4, 9, OperationType.U, from, to).toPredicate(root, query, cb);

        verify(cb).equal(tableIdPath, 4);
        verify(cb).equal(userIdPath, 9);
        verify(cb).equal(opTypePath, OperationType.U);
        verify(cb).greaterThanOrEqualTo(occurredAtPath, from);
        verify(cb).lessThanOrEqualTo(occurredAtPath, to);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        assertThat(captor.getValue()).hasSize(5);
    }
}
