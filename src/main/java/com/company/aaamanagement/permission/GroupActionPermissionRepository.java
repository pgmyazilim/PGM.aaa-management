package com.company.aaamanagement.permission;

import com.company.aaamanagement.domain.GroupActionPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupActionPermissionRepository extends JpaRepository<GroupActionPermission, Integer> {

    Optional<GroupActionPermission> findByAction_ActionIdAndUserGroup_UserGroupId(
            Integer actionId, Integer userGroupId);

    @Query("SELECT p FROM GroupActionPermission p " +
           "JOIN FETCH p.action a JOIN FETCH a.module m " +
           "WHERE p.userGroup.userGroupId = :groupId " +
           "AND (:moduleId IS NULL OR m.moduleId = :moduleId)")
    List<GroupActionPermission> findByGroupAndModule(@Param("groupId") Integer groupId,
                                                      @Param("moduleId") Integer moduleId);

    @Query("SELECT p FROM GroupActionPermission p " +
           "JOIN FETCH p.action a JOIN FETCH a.module m " +
           "WHERE p.userGroup.userGroupId = :groupId " +
           "AND (:moduleId IS NULL OR m.moduleId = :moduleId)")
    Page<GroupActionPermission> findByGroupAndModulePaged(@Param("groupId") Integer groupId,
                                                           @Param("moduleId") Integer moduleId,
                                                           Pageable pageable);
}
