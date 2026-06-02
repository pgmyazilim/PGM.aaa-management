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
                              @RequestParam(required = false) Boolean success,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "50") int size,
                              Model model) {
        model.addAttribute("logs", auditService.listActionLogs(actionId, success, from, to, page, size));
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
                                @RequestParam(required = false) OperationType opType,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "50") int size,
                                Model model) {
        model.addAttribute("audits", auditService.listRecordAudits(tableId, opType, from, to, page, size));
        model.addAttribute("trackedTables", auditService.getAllTrackedTables());
        model.addAttribute("operationTypes", auditService.getOperationTypes());
        model.addAttribute("selectedTableId", tableId);
        model.addAttribute("selectedOpType", opType);
        model.addAttribute("activePage", "record-audits");
        return "audit/record-audits";
    }
}
