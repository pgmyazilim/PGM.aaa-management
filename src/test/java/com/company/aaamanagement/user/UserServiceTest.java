package com.company.aaamanagement.user;

import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.domain.UserGroupMember;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.group.UserGroupMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock GroupRepository groupRepository;
    @Mock UserGroupMemberRepository memberRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    void save_newUserWithoutPassword_throwsIllegalArgument() {
        User form = User.builder().username("yeni").build();
        when(userRepository.existsByUsername("yeni")).thenReturn(false);

        assertThatThrownBy(() -> userService.save(form, "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("parola zorunlu");

        verify(userRepository, never()).save(any());
    }

    @Test
    void save_newUser_hashesPasswordWithEncoder() {
        User form = User.builder().username("yeni").build();
        when(userRepository.existsByUsername("yeni")).thenReturn(false);
        when(passwordEncoder.encode("gizli123")).thenReturn("$2a$10$hash");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User saved = userService.save(form, "gizli123");

        assertThat(saved.getPasswordHash()).isEqualTo("$2a$10$hash");
        assertThat(saved.getLastPasswordChangeUtc()).isNotNull();
        assertThat(saved.getModifiedAtUtc()).isNotNull();
    }

    @Test
    void save_existingUserWithoutPassword_keepsStoredHash() {
        User existing = User.builder().userId(5).username("mevcut")
                .passwordHash("$2a$10$eski")
                .failedLoginCount(3)
                .build();
        User form = User.builder().userId(5).username("mevcut").firstName("Yeni Ad").build();
        when(userRepository.existsByUsernameAndUserIdNot("mevcut", 5)).thenReturn(false);
        when(userRepository.findById(5)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User saved = userService.save(form, null);

        assertThat(saved.getPasswordHash()).isEqualTo("$2a$10$eski");
        assertThat(saved.getFailedLoginCount()).isEqualTo(3);
        assertThat(saved.getFirstName()).isEqualTo("Yeni Ad");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void save_newUserWithBlankUsername_persistsNullNotEmptyString() {
        User form = User.builder().username("   ").build();
        when(passwordEncoder.encode("gizli123")).thenReturn("$2a$10$hash");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User saved = userService.save(form, "gizli123");

        assertThat(saved.getUsername()).isNull();
        verify(userRepository, never()).existsByUsername(any());
    }

    @Test
    void save_newUserWithBlankEmail_persistsNullNotEmptyString() {
        User form = User.builder().username("yeni").emailUser("  ").emailDomain("").build();
        when(userRepository.existsByUsername("yeni")).thenReturn(false);
        when(passwordEncoder.encode("gizli123")).thenReturn("$2a$10$hash");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User saved = userService.save(form, "gizli123");

        assertThat(saved.getEmailUser()).isNull();
        assertThat(saved.getEmailDomain()).isNull();
    }

    @Test
    void save_whenEmailBelongsToAnotherUser_throwsIllegalArgument() {
        User form = User.builder().username("yeni")
                .emailUser("ali").emailDomain("ornek.com").build();
        when(userRepository.existsByUsername("yeni")).thenReturn(false);
        when(userRepository.existsByEmailUserIgnoreCaseAndEmailDomainIgnoreCase("ali", "ornek.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.save(form, "gizli123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("e-posta");

        verify(userRepository, never()).save(any());
    }

    @Test
    void unlock_resetsFailedCountAndLockTimestamp() {
        User user = User.builder().userId(1).failedLoginCount(5)
                .lockedUntilUtc(java.time.LocalDateTime.now()).build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.unlock(1);

        assertThat(user.getFailedLoginCount()).isZero();
        assertThat(user.getLockedUntilUtc()).isNull();
    }

    @Test
    void assignGroupToUser_whenAlreadyMember_throwsIllegalState() {
        when(memberRepository.existsByUser_UserIdAndUserGroup_UserGroupId(1, 2)).thenReturn(true);

        assertThatThrownBy(() -> userService.assignGroupToUser(1, 2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("zaten bu grubun üyesi");

        verify(memberRepository, never()).save(any(UserGroupMember.class));
    }

    @Test
    void assignGroupToUser_whenNotMember_savesMembership() {
        when(memberRepository.existsByUser_UserIdAndUserGroup_UserGroupId(1, 2)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(User.builder().userId(1).build()));
        when(groupRepository.findById(2)).thenReturn(Optional.of(UserGroup.builder().userGroupId(2).build()));

        userService.assignGroupToUser(1, 2);

        verify(memberRepository).save(any(UserGroupMember.class));
    }

    @Test
    void assignGroupToUser_whenUserNotFound_throwsEntityNotFound() {
        when(memberRepository.existsByUser_UserIdAndUserGroup_UserGroupId(99, 2)).thenReturn(false);
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignGroupToUser(99, 2))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void removeGroupFromUser_whenMemberExists_deletesIt() {
        UserGroupMember member = UserGroupMember.builder().userGroupMemberId(5).build();
        when(memberRepository.findByUser_UserIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.of(member));

        userService.removeGroupFromUser(1, 2);

        verify(memberRepository).delete(member);
    }

    @Test
    void removeGroupFromUser_whenNotMember_doesNothing() {
        when(memberRepository.findByUser_UserIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.empty());

        userService.removeGroupFromUser(1, 2);

        verify(memberRepository, never()).delete(any());
    }
}
