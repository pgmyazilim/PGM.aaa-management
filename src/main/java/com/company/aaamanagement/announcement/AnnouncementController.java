package com.company.aaamanagement.announcement;

import com.company.aaamanagement.domain.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public String list(@RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "20") int size,
                       Model model) {
        model.addAttribute("announcements", announcementService.list(search, page, size));
        model.addAttribute("search", search);
        model.addAttribute("activePage", "announcements");
        return "announcement/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        model.addAttribute("allUsers", announcementService.getAllUsers());
        model.addAttribute("activePage", "announcements");
        return "announcement/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("announcement", announcementService.findById(id));
        model.addAttribute("targets", announcementService.getTargets(id));
        model.addAttribute("allUsers", announcementService.getAllUsers());
        model.addAttribute("allGroups", announcementService.getAllGroups());
        model.addAttribute("activePage", "announcements");
        return "announcement/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Announcement announcement,
                       @RequestParam(value = "createdByUserId", required = false) Integer createdByUserId) {
        announcementService.saveAnnouncement(announcement, createdByUserId);
        return "redirect:/announcements";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/announcements";
    }

    @PostMapping("/{id}/targets/add")
    public String addTarget(@PathVariable("id") Integer id,
                            @RequestParam(value = "userId", required = false) Integer userId,
                            @RequestParam(value = "userGroupId", required = false) Integer userGroupId) {
        announcementService.addTarget(id, userId, userGroupId);
        return "redirect:/announcements/" + id + "/edit";
    }

    @PostMapping("/targets/{targetId}/delete")
    public String deleteTarget(@PathVariable("targetId") Integer targetId,
                               @RequestParam("announcementId") Integer announcementId) {
        announcementService.deleteTarget(targetId);
        return "redirect:/announcements/" + announcementId + "/edit";
    }
}
