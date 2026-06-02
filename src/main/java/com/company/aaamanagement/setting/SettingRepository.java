package com.company.aaamanagement.setting;

import com.company.aaamanagement.domain.Setting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettingRepository extends JpaRepository<Setting, Integer> {

    @Query("SELECT s FROM Setting s WHERE :search IS NULL OR " +
           "LOWER(s.settingKey) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Setting> findBySearch(@Param("search") String search, Pageable pageable);
}
