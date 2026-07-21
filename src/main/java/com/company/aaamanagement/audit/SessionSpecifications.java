package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.Session;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

final class SessionSpecifications {

    private SessionSpecifications() {
    }

    static Specification<Session> filters(Integer userId, Boolean open, LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("userId"), userId));
            }
            if (open != null) {
                predicates.add(cb.equal(root.get("open"), open));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("openedAtUtc"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("openedAtUtc"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
