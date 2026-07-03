package com.company.aaamanagement.user;

import com.company.aaamanagement.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<User> users = userService.list(search, page, size);
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "users");
        return "user/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("activePage", "users");
        return "user/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("activePage", "users");
        return "user/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User user,
                       @RequestParam(required = false) String password) {
        userService.save(user, password);
        return "redirect:/users";
    }

    @PostMapping("/{id}/unlock")
    public String unlock(@PathVariable Integer id) {
        userService.unlock(id);
        return "redirect:/users/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        userService.delete(id);
        return "redirect:/users";
    }

    @GetMapping("/{id}/groups")
    public String groupPanel(@PathVariable Integer id, Model model,
                              @RequestHeader(value = "HX-Request", required = false) String htmx) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("memberships", userService.getMemberships(id));
        model.addAttribute("allGroups", userService.getAllGroups());
        if (htmx != null) {
            return "user/group-panel :: panel";
        }
        return "user/group-panel";
    }

    @PostMapping("/{id}/groups/sync")
    public String syncGroups(@PathVariable Integer id,
                              @RequestParam(required = false) List<Integer> groupIds,
                              @RequestHeader(value = "HX-Request", required = false) String htmx) {
        userService.syncGroups(id, groupIds == null ? List.of() : groupIds);
        if (htmx != null) {
            return "user/group-panel :: successMessage";
        }
        return "redirect:/users";
    }
}
