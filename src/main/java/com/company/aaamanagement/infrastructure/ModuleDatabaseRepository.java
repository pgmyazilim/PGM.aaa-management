package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.ModuleDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModuleDatabaseRepository extends JpaRepository<ModuleDatabase, Integer> {

    @Query("SELECT md FROM ModuleDatabase md WHERE :moduleId IS NULL OR md.module.moduleId = :moduleId")
    Page<ModuleDatabase> findByModule(@Param("moduleId") Integer moduleId, Pageable pageable);
}
