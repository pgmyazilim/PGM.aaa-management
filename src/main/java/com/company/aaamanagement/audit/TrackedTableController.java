package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.OperationType;
import com.company.aaamanagement.domain.TrackedTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/audit/tracked-tables")
@RequiredArgsConstructor
public class TrackedTableController {

    private final TrackedTableService trackedTableService;

    @GetMapping
    public String list(@RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "20") int size,
                       Model model) {
        model.addAttribute("trackedTables", trackedTableService.listTrackedTables(search, page, size));
        model.addAttribute("search", search);
        model.addAttribute("error", error);
        model.addAttribute("activePage", "tracked-tables");
        return "audit/tracked-tables";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("trackedTable", new TrackedTable());
        model.addAttribute("operationTypes", OperationType.values());
        model.addAttribute("activePage", "tracked-tables");
        return "audit/tracked-table-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("trackedTable", trackedTableService.findById(id));
        model.addAttribute("operationTypes", OperationType.values());
        model.addAttribute("activePage", "tracked-tables");
        return "audit/tracked-table-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute TrackedTable trackedTable,
                       @RequestParam(value = "actorTypes", required = false) List<String> actorTypes,
                       @RequestParam(value = "recordTypes", required = false) List<String> recordTypes) {
        try {
            trackedTableService.save(trackedTable, actorTypes, recordTypes);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return redirectWithError(e.getMessage());
        }
        return "redirect:/audit/tracked-tables";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        try {
            trackedTableService.delete(id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return redirectWithError(e.getMessage());
        }
        return "redirect:/audit/tracked-tables";
    }

    private String redirectWithError(String message) {
        String safe = (message == null || message.isBlank()) ? "Bir hata oluştu." : message;
        String encoded = UriComponentsBuilder.fromPath("/audit/tracked-tables")
                .queryParam("error", safe)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
        return "redirect:" + encoded;
    }
}
