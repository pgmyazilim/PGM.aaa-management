package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    @Query("SELECT l FROM ActionLog l WHERE " +
           "(:actionId IS NULL OR l.action.actionId = :actionId) AND " +
           "(:actorUserId IS NULL OR l.actorUser.userId = :actorUserId) AND " +
           "(:success IS NULL OR l.success = :success) AND " +
           "(:from IS NULL OR l.occurredAtUtc >= :from) AND " +
           "(:to IS NULL OR l.occurredAtUtc <= :to)")
    @EntityGraph(attributePaths = {"action", "actorUser"})
    Page<ActionLog> findByFilters(@Param("actionId") Integer actionId,
                                   @Param("actorUserId") Integer actorUserId,
                                   @Param("success") Boolean success,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to,
                                   Pageable pageable);
}
