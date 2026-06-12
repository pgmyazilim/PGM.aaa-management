package com.company.aaamanagement.permission;

import com.company.aaamanagement.action.ActionRepository;
import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.GroupActionPermission;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.infrastructure.ModuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock GroupActionPermissionRepository permissionRepository;
    @Mock ActionRepository actionRepository;
    @Mock GroupRepository groupRepository;
    @Mock ModuleRepository moduleRepository;
    @InjectMocks PermissionService permissionService;

    @Test
    void upsertPermission_whenNotExists_createsNewPermission() {
        Action action = Action.builder().actionId(1).build();
        UserGroup group = UserGroup.builder().userGroupId(2).build();
        when(actionRepository.findById(1)).thenReturn(Optional.of(action));
        when(groupRepository.findById(2)).thenReturn(Optional.of(group));
        when(permissionRepository.findByAction_ActionIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.empty());
        when(permissionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        GroupActionPermission result = permissionService.upsertPermission(1, 2, true, null, null);

        assertThat(result.isAllowed()).isTrue();
        verify(permissionRepository).save(any(GroupActionPermission.class));
    }

    @Test
    void upsertPermission_whenExists_updatesExistingPermission() {
        Action action = Action.builder().actionId(1).build();
        UserGroup group = UserGroup.builder().userGroupId(2).build();
        GroupActionPermission existing = GroupActionPermission.builder()
                .groupOperationPermissionId(10).action(action).userGroup(group).allowed(true).build();
        when(actionRepository.findById(1)).thenReturn(Optional.of(action));
        when(groupRepository.findById(2)).thenReturn(Optional.of(group));
        when(permissionRepository.findByAction_ActionIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.of(existing));
        when(permissionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        GroupActionPermission result = permissionService.upsertPermission(1, 2, false, null, null);

        assertThat(result.isAllowed()).isFalse();
        assertThat(result.getGroupOperationPermissionId()).isEqualTo(10);
    }

    @Test
    void togglePermission_whenAllowed_setsToFalse() {
        Action action = Action.builder().actionId(1).build();
        UserGroup group = UserGroup.builder().userGroupId(2).build();
        GroupActionPermission perm = GroupActionPermission.builder()
                .action(action).userGroup(group).allowed(true).build();
        when(permissionRepository.findByAction_ActionIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.of(perm));
        when(permissionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        permissionService.togglePermission(1, 2);

        assertThat(perm.isAllowed()).isFalse();
    }

    @Test
    void getUnassignedActions_delegatesToRepository() {
        Action unassigned = Action.builder().actionId(5).name("Fatura Sil").build();
        when(actionRepository.findUnassignedForGroup(2)).thenReturn(List.of(unassigned));

        List<Action> result = permissionService.getUnassignedActions(2);

        assertThat(result).containsExactly(unassigned);
    }
}
