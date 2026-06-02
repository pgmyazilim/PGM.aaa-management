package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Integer> {

    @Query("SELECT m FROM Module m WHERE " +
           "(:projectId IS NULL OR m.project.projectId = :projectId) AND " +
           "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Module> findByProjectAndSearch(@Param("projectId") Integer projectId,
                                         @Param("search") String search,
                                         Pageable pageable);

    List<Module> findAllByOrderByNameAsc();

    List<Module> findByProject_ProjectIdOrderByNameAsc(Integer projectId);
}
