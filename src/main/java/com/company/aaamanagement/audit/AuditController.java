package com.company.aaamanagement.audit;

import com.company.aaamanagement.common.LocalTimeService;
import com.company.aaamanagement.domain.OperationType;
import lombok.RequiredArgsConstructor;
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
    private final LocalTimeService localTimeService;

    @GetMapping("/action-logs")
    public String actionLogs(@RequestParam(value = "actionId", required = false) Integer actionId,
                              @RequestParam(value = "actorUserId", required = false) Integer actorUserId,
                              @RequestParam(value = "success", required = false) Boolean success,
                              @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                              @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "50") int size,
                              Model model) {
        model.addAttribute("logs", auditService.listActionLogs(actionId, actorUserId, success,
                localTimeService.toUtc(from), localTimeService.toUtc(to), page, size));
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
    public String sessions(@RequestParam(value = "userId", required = false) Integer userId,
                            @RequestParam(value = "open", required = false) Boolean open,
                            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "50") int size,
                            Model model) {
        model.addAttribute("sessions", auditService.listSessions(userId, open,
                localTimeService.toUtc(from), localTimeService.toUtc(to), page, size));
        model.addAttribute("activePage", "sessions");
        return "audit/sessions";
    }

    @GetMapping("/record-audits")
    public String recordAudits(@RequestParam(value = "tableId", required = false) Integer tableId,
                                @RequestParam(value = "actorUserId", required = false) Integer actorUserId,
                                @RequestParam(value = "opType", required = false) OperationType opType,
                                @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "50") int size,
                                Model model) {
        model.addAttribute("audits", auditService.listRecordAudits(tableId, actorUserId, opType,
                localTimeService.toUtc(from), localTimeService.toUtc(to), page, size));
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
    public String recordAuditDetail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("audit", auditService.findRecordAuditById(id));
        model.addAttribute("activePage", "record-audits");
        return "audit/record-audit-detail";
    }
}
