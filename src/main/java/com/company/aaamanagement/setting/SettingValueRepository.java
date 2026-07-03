package com.company.aaamanagement.setting;

import com.company.aaamanagement.domain.SettingValue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingValueRepository extends JpaRepository<SettingValue, Integer> {

    @EntityGraph(attributePaths = {"user", "userGroup"})
    List<SettingValue> findBySetting_SettingId(Integer settingId);

    List<SettingValue> findByUser_UserId(Integer userId);

    List<SettingValue> findByUserGroup_UserGroupId(Integer groupId);
}
