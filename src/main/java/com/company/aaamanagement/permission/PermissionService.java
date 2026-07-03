package com.company.aaamanagement.permission;

import com.company.aaamanagement.action.ActionRepository;
import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.GroupActionPermission;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.infrastructure.ModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final GroupActionPermissionRepository permissionRepository;
    private final ActionRepository actionRepository;
    private final GroupRepository groupRepository;
    private final ModuleRepository moduleRepository;

    public List<GroupActionPermission> getPermissionsForGroup(Integer groupId, Integer moduleId) {
        return permissionRepository.findByGroupAndModule(groupId, moduleId);
    }

    public List<UserGroup> getAllGroups() {
        return groupRepository.findAllByOrderByNameAsc();
    }

    public List<?> getAllModules() {
        return moduleRepository.findAllByOrderByNameAsc();
    }

    public List<Action> getUnassignedActions(Integer groupId) {
        return actionRepository.findUnassignedForGroup(groupId);
    }

    @Transactional
    public GroupActionPermission upsertPermission(Integer actionId, Integer groupId, boolean allowed,
                                                   LocalDateTime expiresAtUtc, Short allowedExecutionCount) {
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new EntityNotFoundException("İşlem bulunamadı: " + actionId));
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grup bulunamadı: " + groupId));

        GroupActionPermission perm = permissionRepository
                .findByAction_ActionIdAndUserGroup_UserGroupId(actionId, groupId)
                .orElse(GroupActionPermission.builder().action(action).userGroup(group).build());

        perm.setAllowed(allowed);
        perm.setExpiresAtUtc(expiresAtUtc);
        perm.setAllowedExecutionCount(allowedExecutionCount);
        perm.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return permissionRepository.save(perm);
    }

    @Transactional
    public void togglePermission(Integer actionId, Integer groupId) {
        GroupActionPermission perm = permissionRepository
                .findByAction_ActionIdAndUserGroup_UserGroupId(actionId, groupId)
                .orElseGet(() -> {
                    Action action = actionRepository.findById(actionId)
                            .orElseThrow(() -> new EntityNotFoundException("İşlem bulunamadı: " + actionId));
                    UserGroup group = groupRepository.findById(groupId)
                            .orElseThrow(() -> new EntityNotFoundException("Grup bulunamadı: " + groupId));
                    return GroupActionPermission.builder()
                            .action(action).userGroup(group).allowed(false).build();
                });
        perm.setAllowed(!perm.isAllowed());
        perm.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        permissionRepository.save(perm);
    }

    @Transactional
    public void delete(Integer permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}
