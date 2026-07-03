package com.company.aaamanagement.constraint;

import com.company.aaamanagement.domain.ActionConstraintGroupValue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionConstraintGroupValueRepository extends JpaRepository<ActionConstraintGroupValue, Integer> {

    @EntityGraph(attributePaths = "userGroup")
    List<ActionConstraintGroupValue> findByActionConstraint_ActionConstraintId(Integer constraintId);

    Optional<ActionConstraintGroupValue> findByActionConstraint_ActionConstraintIdAndUserGroup_UserGroupId(
            Integer constraintId, Integer groupId);
}
