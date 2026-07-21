package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SessionRepository extends JpaRepository<Session, Long>, JpaSpecificationExecutor<Session> {

    @EntityGraph(attributePaths = {"user"})
    Page<Session> findAll(Specification<Session> spec, Pageable pageable);

    @Modifying
    @Query("UPDATE Session s SET s.open = false, s.normalClose = false, " +
           "s.closedAtUtc = :now, s.modifiedAtUtc = :now " +
           "WHERE s.open = true AND s.expiresAtUtc IS NOT NULL AND s.expiresAtUtc < :now")
    int closeExpiredSessions(@Param("now") LocalDateTime now);
}
