package com.company.aaamanagement.group;

import com.company.aaamanagement.domain.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<UserGroup, Integer> {

    @Query("SELECT g FROM UserGroup g WHERE :search IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<UserGroup> findBySearch(@Param("search") String search, Pageable pageable);

    List<UserGroup> findAllByOrderByNameAsc();

    boolean existsByName(String name);

    boolean existsByNameAndUserGroupIdNot(String name, Integer userGroupId);
}
