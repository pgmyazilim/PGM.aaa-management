package com.company.aaamanagement.setting;

import com.company.aaamanagement.domain.Setting;
import com.company.aaamanagement.domain.SettingValue;
import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private final SettingValueRepository settingValueRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public Page<Setting> list(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("settingKey"));
        return settingRepository.findBySearch(search, pageable);
    }

    public Setting findById(Integer id) {
        return settingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ayar bulunamadı: " + id));
    }

    @Transactional
    public Setting saveSetting(Setting setting) {
        setting.setRowVersionUtc(LocalDateTime.now(ZoneOffset.UTC));
        return settingRepository.save(setting);
    }

    @Transactional
    public void deleteSetting(Integer id) {
        settingRepository.deleteById(id);
    }

    public List<SettingValue> getValues(Integer settingId) {
        return settingValueRepository.findBySetting_SettingId(settingId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("lastName", "firstName"));
    }

    public List<UserGroup> getAllGroups() {
        return groupRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public SettingValue saveSettingValue(SettingValue value) {
        boolean hasUser = value.getUser() != null;
        boolean hasGroup = value.getUserGroup() != null;
        if (hasUser == hasGroup) {
            throw new IllegalArgumentException(
                    "Ayar değeri tam olarak bir hedef içermeli: UserId VEYA UserGroupId.");
        }
        value.setRowVersionUtc(LocalDateTime.now(ZoneOffset.UTC));
        return settingValueRepository.save(value);
    }

    @Transactional
    public void deleteSettingValue(Integer id) {
        settingValueRepository.deleteById(id);
    }
}
