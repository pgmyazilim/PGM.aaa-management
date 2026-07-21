package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.RecordAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RecordAuditRepository extends JpaRepository<RecordAudit, Long>, JpaSpecificationExecutor<RecordAudit> {

    // detay ekranı trackedTable/actorUser/session/actionLog(+action) alanlarına eriştiği için
    // birlikte fetch edilir (open-in-view kapalı, aksi halde LazyInitializationException).
    @Override
    @EntityGraph(attributePaths = {"trackedTable", "actorUser", "session", "actionLog", "actionLog.action"})
    Optional<RecordAudit> findById(Long id);

    @EntityGraph(attributePaths = {"trackedTable", "actorUser"})
    Page<RecordAudit> findAll(Specification<RecordAudit> spec, Pageable pageable);

    boolean existsByTrackedTable_TrackedTableId(Integer trackedTableId);
}
