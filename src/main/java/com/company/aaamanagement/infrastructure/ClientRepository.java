package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    @Query("SELECT c FROM Client c WHERE :search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Client> findBySearch(@Param("search") String search, Pageable pageable);

    List<Client> findAllByOrderByNameAsc();

    boolean existsByName(String name);

    boolean existsByNameAndClientIdNot(String name, Integer clientId);
}
