package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.TrackedTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrackedTableRepository extends JpaRepository<TrackedTable, Integer> {

    List<TrackedTable> findAllByOrderByNameAsc();

    @Query("SELECT t FROM TrackedTable t WHERE :search IS NULL OR " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<TrackedTable> findBySearch(@Param("search") String search, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndTrackedTableIdNot(String name, Integer trackedTableId);
}
