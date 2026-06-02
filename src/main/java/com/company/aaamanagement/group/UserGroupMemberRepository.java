package com.company.aaamanagement.group;

import com.company.aaamanagement.domain.UserGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGroupMemberRepository extends JpaRepository<UserGroupMember, Integer> {

    List<UserGroupMember> findByUser_UserId(Integer userId);

    List<UserGroupMember> findByUserGroup_UserGroupId(Integer userGroupId);

    boolean existsByUser_UserIdAndUserGroup_UserGroupId(Integer userId, Integer userGroupId);

    Optional<UserGroupMember> findByUser_UserIdAndUserGroup_UserGroupId(Integer userId, Integer userGroupId);

    @Query("SELECT COUNT(m) FROM UserGroupMember m WHERE m.userGroup.userGroupId = :groupId")
    long countByGroupId(@Param("groupId") Integer groupId);
}
