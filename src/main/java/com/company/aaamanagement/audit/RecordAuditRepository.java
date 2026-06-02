package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.RecordAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RecordAuditRepository extends JpaRepository<RecordAudit, Long> {

    @Query("SELECT r FROM RecordAudit r WHERE " +
           "(:tableId IS NULL OR r.trackedTable.trackedTableId = :tableId) AND " +
           "(:opType IS NULL OR r.operationType = :opType) AND " +
           "(:from IS NULL OR r.occurredAtUtc >= :from) AND " +
           "(:to IS NULL OR r.occurredAtUtc <= :to)")
    Page<RecordAudit> findByFilters(@Param("tableId") Integer tableId,
                                     @Param("opType") OperationType opType,
                                     @Param("from") LocalDateTime from,
                                     @Param("to") LocalDateTime to,
                                     Pageable pageable);
}
