package com.company.aaamanagement.permission;

import com.company.aaamanagement.domain.GroupActionPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public String matrix(@RequestParam(required = false) Integer groupId,
                         @RequestParam(required = false) Integer moduleId,
                         Model model) {
        List<GroupActionPermission> permissions = groupId != null
                ? permissionService.getPermissionsForGroup(groupId, moduleId)
                : List.of();
        model.addAttribute("permissions", permissions);
        model.addAttribute("groups", permissionService.getAllGroups());
        model.addAttribute("modules", permissionService.getAllModules());
        model.addAttribute("selectedGroupId", groupId);
        model.addAttribute("selectedModuleId", moduleId);
        model.addAttribute("activePage", "permissions");
        return "permission/matrix";
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggle(
            @RequestParam Integer actionId,
            @RequestParam Integer groupId,
            @RequestHeader(value = "HX-Request", required = false) String htmx) {
        permissionService.togglePermission(actionId, groupId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/upsert")
    public String upsert(@RequestParam Integer actionId,
                         @RequestParam Integer groupId,
                         @RequestParam boolean allowed,
                         @RequestParam(required = false) String expiresAtUtc,
                         @RequestParam(required = false) Short allowedExecutionCount) {
        LocalDateTime expires = expiresAtUtc != null && !expiresAtUtc.isBlank()
                ? LocalDateTime.parse(expiresAtUtc) : null;
        permissionService.upsertPermission(actionId, groupId, allowed, expires, allowedExecutionCount);
        return "redirect:/permissions?groupId=" + groupId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
                         @RequestParam(required = false) Integer groupId) {
        permissionService.delete(id);
        return groupId != null ? "redirect:/permissions?groupId=" + groupId : "redirect:/permissions";
    }
}
