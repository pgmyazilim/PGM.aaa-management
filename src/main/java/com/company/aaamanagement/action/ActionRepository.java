package com.company.aaamanagement.action;

import com.company.aaamanagement.domain.Action;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Integer> {

    @Query("SELECT a FROM Action a WHERE " +
           "(:moduleId IS NULL OR a.module.moduleId = :moduleId) AND " +
           "(:search IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.actionKey) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Action> findByModuleAndSearch(@Param("moduleId") Integer moduleId,
                                       @Param("search") String search,
                                       Pageable pageable);

    List<Action> findByModule_ModuleIdOrderByNameAsc(Integer moduleId);

    boolean existsByActionKey(String actionKey);

    boolean existsByActionKeyAndActionIdNot(String actionKey, Integer actionId);
}
