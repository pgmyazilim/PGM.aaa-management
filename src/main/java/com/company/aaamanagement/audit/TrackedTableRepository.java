package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.TrackedTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackedTableRepository extends JpaRepository<TrackedTable, Integer> {

    List<TrackedTable> findAllByOrderByNameAsc();
}
