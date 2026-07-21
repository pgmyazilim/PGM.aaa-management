package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.RecordAudit;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

final class RecordAuditSpecifications {

    private RecordAuditSpecifications() {
    }

    static Specification<RecordAudit> filters(Integer tableId, Integer actorUserId, OperationType opType,
                                               LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (tableId != null) {
                predicates.add(cb.equal(root.get("trackedTable").get("trackedTableId"), tableId));
            }
            if (actorUserId != null) {
                predicates.add(cb.equal(root.get("actorUser").get("userId"), actorUserId));
            }
            if (opType != null) {
                predicates.add(cb.equal(root.get("operationType"), opType));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("occurredAtUtc"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("occurredAtUtc"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
