package com.company.aaamanagement.constraint;

import com.company.aaamanagement.domain.ActionConstraint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionConstraintRepository extends JpaRepository<ActionConstraint, Integer> {

    @Query("SELECT c FROM ActionConstraint c WHERE " +
           "(:actionId IS NULL OR c.action.actionId = :actionId) AND " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    @EntityGraph(attributePaths = "action")
    Page<ActionConstraint> findByActionAndSearch(@Param("actionId") Integer actionId,
                                                  @Param("search") String search,
                                                  Pageable pageable);

    List<ActionConstraint> findByAction_ActionIdOrderByNameAsc(Integer actionId);
}
