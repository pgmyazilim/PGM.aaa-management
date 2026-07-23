package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.ModuleDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModuleDatabaseRepository extends JpaRepository<ModuleDatabase, Integer> {

    @Query("SELECT md FROM ModuleDatabase md WHERE " +
           "(:search IS NULL OR LOWER(md.databaseName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(md.databaseAlias) LIKE LOWER(CONCAT('%', :search, '%')))")
    @EntityGraph(attributePaths = {"databaseServer", "databaseCredential"})
    Page<ModuleDatabase> findBySearch(@Param("search") String search, Pageable pageable);
}
