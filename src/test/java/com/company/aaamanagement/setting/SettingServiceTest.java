package com.company.aaamanagement.setting;

import com.company.aaamanagement.domain.Setting;
import com.company.aaamanagement.domain.SettingValue;
import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {

    @Mock SettingRepository settingRepository;
    @Mock SettingValueRepository settingValueRepository;
    @Mock UserRepository userRepository;
    @Mock GroupRepository groupRepository;
    @InjectMocks SettingService settingService;

    @Test
    void saveSettingValue_whenBothUserAndGroup_throwsIllegalArgument() {
        SettingValue value = SettingValue.builder()
                .setting(Setting.builder().settingId(1).build())
                .user(User.builder().userId(1).build())
                .userGroup(UserGroup.builder().userGroupId(2).build())
                .value("test")
                .build();

        assertThatThrownBy(() -> settingService.saveSettingValue(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tam olarak bir hedef");

        verify(settingValueRepository, never()).save(any());
    }

    @Test
    void saveSettingValue_whenNeitherUserNorGroup_throwsIllegalArgument() {
        SettingValue value = SettingValue.builder()
                .setting(Setting.builder().settingId(1).build())
                .value("test")
                .build();

        assertThatThrownBy(() -> settingService.saveSettingValue(value))
                .isInstanceOf(IllegalArgumentException.class);

        verify(settingValueRepository, never()).save(any());
    }

    @Test
    void saveSettingValue_withOnlyUser_savesSuccessfully() {
        SettingValue value = SettingValue.builder()
                .setting(Setting.builder().settingId(1).build())
                .user(User.builder().userId(1).build())
                .value("test")
                .build();
        when(settingValueRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        settingService.saveSettingValue(value);

        verify(settingValueRepository).save(value);
    }

    @Test
    void saveSettingValue_withOnlyGroup_savesSuccessfully() {
        SettingValue value = SettingValue.builder()
                .setting(Setting.builder().settingId(1).build())
                .userGroup(UserGroup.builder().userGroupId(2).build())
                .value("test")
                .build();
        when(settingValueRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        settingService.saveSettingValue(value);

        verify(settingValueRepository).save(value);
    }
}
