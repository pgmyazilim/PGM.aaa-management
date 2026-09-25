package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.ExternalUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExternalUrlRepository extends JpaRepository<ExternalUrl, Integer> {

    @Query("SELECT e FROM ExternalUrl e WHERE " +
           "(:projectId IS NULL OR e.project.projectId = :projectId) AND " +
           "(:search IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    @EntityGraph(attributePaths = "project")
    Page<ExternalUrl> findByProjectAndSearch(@Param("projectId") Integer projectId,
                                              @Param("search") String search,
                                              Pageable pageable);
}
