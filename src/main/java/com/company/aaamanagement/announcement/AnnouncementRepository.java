package com.company.aaamanagement.announcement;

import com.company.aaamanagement.domain.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    @Query("SELECT a FROM Announcement a WHERE :search IS NULL OR " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    @EntityGraph(attributePaths = {"createdByUser"})
    Page<Announcement> findBySearch(@Param("search") String search, Pageable pageable);
}
