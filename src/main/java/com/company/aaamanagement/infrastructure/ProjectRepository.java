package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    @Query("SELECT p FROM Project p WHERE :search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Project> findBySearch(@Param("search") String search, Pageable pageable);

    List<Project> findAllByOrderByNameAsc();
}
