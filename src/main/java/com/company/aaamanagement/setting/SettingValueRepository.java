package com.company.aaamanagement.setting;

import com.company.aaamanagement.domain.SettingValue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettingValueRepository extends JpaRepository<SettingValue, Integer> {

    @EntityGraph(attributePaths = {"user", "userGroup"})
    List<SettingValue> findBySetting_SettingId(Integer settingId);

    Optional<SettingValue> findBySetting_SettingIdAndUser_UserId(Integer settingId, Integer userId);

    Optional<SettingValue> findBySetting_SettingIdAndUserGroup_UserGroupId(Integer settingId, Integer userGroupId);

    List<SettingValue> findByUser_UserId(Integer userId);

    List<SettingValue> findByUserGroup_UserGroupId(Integer groupId);
}
