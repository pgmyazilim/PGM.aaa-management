package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.ActionLog;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * These tests guard the fix for the "catch-all query" anti-pattern: a spec built with no
 * filters must add zero predicates (not "column = null OR ..."), otherwise SQL Server is
 * back to compiling a single plan that can never use a targeted index.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class ActionLogSpecificationsTest {

    private final Root<ActionLog> root = mock(Root.class);
    private final CriteriaQuery<?> query = mock(CriteriaQuery.class);
    private final CriteriaBuilder cb = mock(CriteriaBuilder.class);

    @Test
    void filters_withNoCriteria_addsNoPredicatesAndTouchesNoColumn() {
        when(cb.and(any(Predicate[].class))).thenAnswer(inv -> mock(Predicate.class));

        ActionLogSpecifications.filters(null, null, null, null, null).toPredicate(root, query, cb);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        assertThat(captor.getValue()).isEmpty();
        verifyNoInteractions(root);
    }

    @Test
    void filters_withAllCriteria_addsExactlyOnePredicatePerFilter() {
        Path actionPath = mock(Path.class);
        Path actionIdPath = mock(Path.class);
        when(root.get("action")).thenReturn(actionPath);
        when(actionPath.get("actionId")).thenReturn(actionIdPath);

        Path actorUserPath = mock(Path.class);
        Path userIdPath = mock(Path.class);
        when(root.get("actorUser")).thenReturn(actorUserPath);
        when(actorUserPath.get("userId")).thenReturn(userIdPath);

        Path successPath = mock(Path.class);
        when(root.get("success")).thenReturn(successPath);

        Path occurredAtPath = mock(Path.class);
        when(root.get("occurredAtUtc")).thenReturn(occurredAtPath);

        when(cb.and(any(Predicate[].class))).thenAnswer(inv -> mock(Predicate.class));

        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);

        ActionLogSpecifications.filters(5, 9, true, from, to).toPredicate(root, query, cb);

        verify(cb).equal(actionIdPath, 5);
        verify(cb).equal(userIdPath, 9);
        verify(cb).equal(successPath, true);
        verify(cb).greaterThanOrEqualTo(occurredAtPath, from);
        verify(cb).lessThanOrEqualTo(occurredAtPath, to);

        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        assertThat(captor.getValue()).hasSize(5);
    }

    @Test
    void filters_withOnlyActorUserId_addsSinglePredicate() {
        Path actorUserPath = mock(Path.class);
        Path userIdPath = mock(Path.class);
        when(root.get("actorUser")).thenReturn(actorUserPath);
        when(actorUserPath.get("userId")).thenReturn(userIdPath);
        when(cb.and(any(Predicate[].class))).thenAnswer(inv -> mock(Predicate.class));

        ActionLogSpecifications.filters(null, 9, null, null, null).toPredicate(root, query, cb);

        verify(cb).equal(userIdPath, 9);
        verify(root, never()).get("action");
        verify(root, never()).get("occurredAtUtc");
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb).and(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }
}
