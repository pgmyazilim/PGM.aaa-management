package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long>, JpaSpecificationExecutor<ActionLog> {

    @EntityGraph(attributePaths = {"action", "actorUser"})
    Page<ActionLog> findAll(Specification<ActionLog> spec, Pageable pageable);
}
