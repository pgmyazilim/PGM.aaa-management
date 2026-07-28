package com.company.aaamanagement.setting;

import com.company.aaamanagement.domain.Setting;
import com.company.aaamanagement.domain.SettingValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    public String list(@RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "20") int size,
                       Model model) {
        Page<Setting> settings = settingService.list(search, page, size);
        model.addAttribute("settings", settings);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "settings");
        return "setting/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("setting", new Setting());
        model.addAttribute("activePage", "settings");
        return "setting/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("setting", settingService.findById(id));
        model.addAttribute("values", settingService.getValues(id));
        model.addAttribute("allUsers", settingService.getAllUsers());
        model.addAttribute("allGroups", settingService.getAllGroups());
        model.addAttribute("activePage", "settings");
        return "setting/form";
    }

    @PostMapping("/save")
    public String saveSetting(@ModelAttribute Setting setting) {
        settingService.saveSetting(setting);
        return "redirect:/settings";
    }

    @PostMapping("/{id}/delete")
    public String deleteSetting(@PathVariable("id") Integer id) {
        settingService.deleteSetting(id);
        return "redirect:/settings";
    }

    @PostMapping("/{settingId}/values/save")
    public String saveValue(@PathVariable("settingId") Integer settingId,
                             @RequestParam(value = "userId", required = false) Integer userId,
                             @RequestParam(value = "userGroupId", required = false) Integer userGroupId,
                             @RequestParam("value") String value) {
        Setting setting = settingService.findById(settingId);
        SettingValue sv = new SettingValue();
        sv.setSetting(setting);
        sv.setValue(value);
        if (userId != null) {
            sv.setUser(settingService.getAllUsers().stream()
                    .filter(u -> u.getUserId().equals(userId)).findFirst().orElseThrow());
        } else if (userGroupId != null) {
            sv.setUserGroup(settingService.getAllGroups().stream()
                    .filter(g -> g.getUserGroupId().equals(userGroupId)).findFirst().orElseThrow());
        }
        settingService.saveSettingValue(sv);
        return "redirect:/settings/" + settingId + "/edit";
    }

    @PostMapping("/values/{valueId}/delete")
    public String deleteValue(@PathVariable("valueId") Integer valueId,
                               @RequestParam("settingId") Integer settingId) {
        settingService.deleteSettingValue(valueId);
        return "redirect:/settings/" + settingId + "/edit";
    }
}
