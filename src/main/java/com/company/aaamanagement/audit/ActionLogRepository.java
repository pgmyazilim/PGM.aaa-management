package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long>, JpaSpecificationExecutor<ActionLog> {

    @EntityGraph(attributePaths = {"action", "actorUser"})
    Page<ActionLog> findAll(Specification<ActionLog> spec, Pageable pageable);

    // detay ekranı action.module/actorUser/session alanlarına eriştiği için
    // birlikte fetch edilir (open-in-view kapalı, aksi halde LazyInitializationException).
    @Override
    @EntityGraph(attributePaths = {"action", "action.module", "actorUser", "session"})
    Optional<ActionLog> findById(Long id);
}
