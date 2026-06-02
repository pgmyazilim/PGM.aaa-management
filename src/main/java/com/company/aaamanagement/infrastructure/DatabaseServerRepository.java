package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.DatabaseServer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatabaseServerRepository extends JpaRepository<DatabaseServer, Integer> {

    @Query("SELECT s FROM DatabaseServer s WHERE :search IS NULL OR " +
           "LOWER(s.serverHostname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.serverIpAddress) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<DatabaseServer> findBySearch(@Param("search") String search, Pageable pageable);

    List<DatabaseServer> findAllByOrderByServerHostnameAsc();
}
