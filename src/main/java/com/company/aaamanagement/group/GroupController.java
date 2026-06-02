package com.company.aaamanagement.group;

import com.company.aaamanagement.domain.UserGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<UserGroup> groups = groupService.list(search, page, size);
        model.addAttribute("groups", groups);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "groups");
        return "group/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("group", new UserGroup());
        model.addAttribute("activePage", "groups");
        return "group/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("group", groupService.findById(id));
        model.addAttribute("activePage", "groups");
        return "group/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute UserGroup group) {
        groupService.save(group);
        return "redirect:/groups";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        groupService.delete(id);
        return "redirect:/groups";
    }
}
