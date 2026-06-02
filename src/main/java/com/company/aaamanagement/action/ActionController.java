package com.company.aaamanagement.action;

import com.company.aaamanagement.domain.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    @GetMapping
    public String list(@RequestParam(required = false) Integer moduleId,
                       @RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<Action> actions = actionService.list(moduleId, search, page, size);
        model.addAttribute("actions", actions);
        model.addAttribute("modules", actionService.getAllModules());
        model.addAttribute("selectedModuleId", moduleId);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "actions");
        return "action/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Integer moduleId, Model model) {
        Action action = new Action();
        model.addAttribute("action", action);
        model.addAttribute("modules", actionService.getAllModules());
        model.addAttribute("selectedModuleId", moduleId);
        model.addAttribute("activePage", "actions");
        return "action/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("action", actionService.findById(id));
        model.addAttribute("modules", actionService.getAllModules());
        model.addAttribute("activePage", "actions");
        return "action/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Action action) {
        actionService.save(action);
        return "redirect:/actions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        actionService.delete(id);
        return "redirect:/actions";
    }
}
