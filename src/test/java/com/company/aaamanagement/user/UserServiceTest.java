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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock GroupRepository groupRepository;
    @Mock UserGroupMemberRepository memberRepository;
    @InjectMocks UserService userService;

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
