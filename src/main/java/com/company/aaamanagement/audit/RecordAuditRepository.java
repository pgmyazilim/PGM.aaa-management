package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.RecordAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RecordAuditRepository extends JpaRepository<RecordAudit, Long> {

    // detay ekranı trackedTable/actorUser/session/actionLog(+action) alanlarına eriştiği için
    // birlikte fetch edilir (open-in-view kapalı, aksi halde LazyInitializationException).
    @Override
    @EntityGraph(attributePaths = {"trackedTable", "actorUser", "session", "actionLog", "actionLog.action"})
    Optional<RecordAudit> findById(Long id);

    @Query("SELECT r FROM RecordAudit r WHERE " +
           "(:tableId IS NULL OR r.trackedTable.trackedTableId = :tableId) AND " +
           "(:actorUserId IS NULL OR r.actorUser.userId = :actorUserId) AND " +
           "(:opType IS NULL OR r.operationType = :opType) AND " +
           "(:search IS NULL OR LOWER(r.extraInfo) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:from IS NULL OR r.occurredAtUtc >= :from) AND " +
           "(:to IS NULL OR r.occurredAtUtc <= :to)")
    @EntityGraph(attributePaths = {"trackedTable", "actorUser"})
    Page<RecordAudit> findByFilters(@Param("tableId") Integer tableId,
                                     @Param("actorUserId") Integer actorUserId,
                                     @Param("opType") OperationType opType,
                                     @Param("search") String search,
                                     @Param("from") LocalDateTime from,
                                     @Param("to") LocalDateTime to,
                                     Pageable pageable);

    boolean existsByTrackedTable_TrackedTableId(Integer trackedTableId);
}
