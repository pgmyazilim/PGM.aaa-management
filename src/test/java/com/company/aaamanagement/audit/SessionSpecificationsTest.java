package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.Session;
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
class SessionSpecificationsTest {

    private final Root<Session> root = mock(Root.class);
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final CriteriaBuilder cb = mock(CriteriaBuilder.class);

    @Test
    void filters_withNoCriteria_addsNoPredicatesAndTouchesNoColumn() {
        when(cb.and(any(Predicate[].class))).thenAnswer(inv -> mock(Predicate.class));

        SessionSpecifications.filters(null, null, null, null).toPredicate(root, query, cb);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        assertThat(captor.getValue()).isEmpty();
        verifyNoInteractions(root);
    }

    @Test
    void filters_withAllCriteria_addsExactlyOnePredicatePerFilter() {
        Path userPath = mock(Path.class);
        Path userIdPath = mock(Path.class);
        when(root.get("user")).thenReturn(userPath);
        when(userPath.get("userId")).thenReturn(userIdPath);

        Path openPath = mock(Path.class);
        when(root.get("open")).thenReturn(openPath);

        Path openedAtPath = mock(Path.class);
        when(root.get("openedAtUtc")).thenReturn(openedAtPath);

        when(cb.and(any(Predicate[].class))).thenAnswer(inv -> mock(Predicate.class));

        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);

        SessionSpecifications.filters(9, true, from, to).toPredicate(root, query, cb);

        verify(cb).equal(userIdPath, 9);
        verify(cb).equal(openPath, true);
        verify(cb).greaterThanOrEqualTo(openedAtPath, from);
        verify(cb).lessThanOrEqualTo(openedAtPath, to);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        assertThat(captor.getValue()).hasSize(4);
    }
}
