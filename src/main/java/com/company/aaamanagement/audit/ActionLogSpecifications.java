package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.ActionLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

final class ActionLogSpecifications {

    private ActionLogSpecifications() {
    }

    static Specification<ActionLog> filters(Integer actionId, Integer actorUserId, Boolean success,
                                             LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (actionId != null) {
                predicates.add(cb.equal(root.get("action").get("actionId"), actionId));
            }
            if (actorUserId != null) {
                predicates.add(cb.equal(root.get("actorUser").get("userId"), actorUserId));
            }
            if (success != null) {
                predicates.add(cb.equal(root.get("success"), success));
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
