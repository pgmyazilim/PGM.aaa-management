package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/action-logs")
    public String actionLogs(@RequestParam(required = false) Integer actionId,
                              @RequestParam(required = false) Integer actorUserId,
                              @RequestParam(required = false) Boolean success,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "50") int size,
                              Model model) {
        model.addAttribute("logs", auditService.listActionLogs(actionId, actorUserId, success, from, to, page, size));
        model.addAttribute("allActions", auditService.getAllActions());
        model.addAttribute("allUsers", auditService.getAllUsersForFilter());
        model.addAttribute("selectedActionId", actionId);
        model.addAttribute("selectedActorUserId", actorUserId);
        model.addAttribute("selectedSuccess", success);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("activePage", "action-logs");
        return "audit/action-logs";
    }

    @GetMapping("/sessions")
    public String sessions(@RequestParam(required = false) Integer userId,
                            @RequestParam(required = false) Boolean open,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "50") int size,
                            Model model) {
        model.addAttribute("sessions", auditService.listSessions(userId, open, from, to, page, size));
        model.addAttribute("activePage", "sessions");
        return "audit/sessions";
    }

    @GetMapping("/record-audits")
    public String recordAudits(@RequestParam(required = false) Integer tableId,
                                @RequestParam(required = false) Integer actorUserId,
                                @RequestParam(required = false) OperationType opType,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "50") int size,
                                Model model) {
        model.addAttribute("audits", auditService.listRecordAudits(tableId, actorUserId, opType, from, to, page, size));
        model.addAttribute("trackedTables", auditService.getAllTrackedTables());
        model.addAttribute("operationTypes", auditService.getOperationTypes());
        model.addAttribute("allUsers", auditService.getAllUsersForFilter());
        model.addAttribute("selectedTableId", tableId);
        model.addAttribute("selectedActorUserId", actorUserId);
        model.addAttribute("selectedOpType", opType);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("activePage", "record-audits");
        return "audit/record-audits";
    }

    @GetMapping("/record-audits/{id}")
    public String recordAuditDetail(@PathVariable Long id, Model model) {
        model.addAttribute("audit", auditService.findRecordAuditById(id));
        model.addAttribute("activePage", "record-audits");
        return "audit/record-audit-detail";
    }
}
