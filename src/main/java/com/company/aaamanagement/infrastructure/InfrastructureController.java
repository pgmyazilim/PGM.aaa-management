package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.*;
import com.company.aaamanagement.domain.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/infrastructure")
@RequiredArgsConstructor
public class InfrastructureController {

    private final InfrastructureService infraService;

    // --- Projects ---
    @GetMapping("/projects")
    public String projects(@RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {
        model.addAttribute("projects", infraService.listProjects(search, page, size));
        model.addAttribute("search", search);
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/projects";
    }

    @GetMapping("/projects/new")
    public String newProject(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/project-form";
    }

    @GetMapping("/projects/{id}/edit")
    public String editProject(@PathVariable Integer id, Model model) {
        model.addAttribute("project", infraService.findProjectById(id));
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/project-form";
    }

    @PostMapping("/projects/save")
    public String saveProject(@ModelAttribute Project project) {
        infraService.saveProject(project);
        return "redirect:/infrastructure/projects";
    }

    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable Integer id) {
        infraService.deleteProject(id);
        return "redirect:/infrastructure/projects";
    }

    // --- Modules ---
    @GetMapping("/modules")
    public String modules(@RequestParam(required = false) Integer projectId,
                           @RequestParam(required = false) String search,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           Model model) {
        model.addAttribute("modules", infraService.listModules(projectId, search, page, size));
        model.addAttribute("projects", infraService.getAllProjects());
        model.addAttribute("selectedProjectId", projectId);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/modules";
    }

    @GetMapping("/modules/new")
    public String newModule(Model model) {
        model.addAttribute("module", new Module());
        model.addAttribute("projects", infraService.getAllProjects());
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/module-form";
    }

    @GetMapping("/modules/{id}/edit")
    public String editModule(@PathVariable Integer id, Model model) {
        model.addAttribute("module", infraService.findModuleById(id));
        model.addAttribute("projects", infraService.getAllProjects());
        model.addAttribute("moduleDatabases", infraService.getModuleDatabases(id));
        model.addAttribute("servers", infraService.getAllServers());
        model.addAttribute("credentials", infraService.getAllCredentials());
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/module-form";
    }

    @PostMapping("/modules/save")
    public String saveModule(@ModelAttribute Module module) {
        infraService.saveModule(module);
        return "redirect:/infrastructure/modules";
    }

    @PostMapping("/modules/{id}/delete")
    public String deleteModule(@PathVariable Integer id) {
        infraService.deleteModule(id);
        return "redirect:/infrastructure/modules";
    }

    @PostMapping("/modules/{id}/databases/save")
    public String addModuleDatabase(@PathVariable Integer id,
                                     @RequestParam(required = false) Integer databaseServerId,
                                     @RequestParam(required = false) Integer databaseCredentialId,
                                     @RequestParam String databaseName,
                                     @RequestParam(required = false) String databaseAlias) {
        infraService.addModuleDatabase(id, databaseServerId, databaseCredentialId, databaseName, databaseAlias);
        return "redirect:/infrastructure/modules/" + id + "/edit";
    }

    @PostMapping("/module-databases/{id}/delete")
    public String deleteModuleDatabase(@PathVariable Integer id,
                                        @RequestParam Integer moduleId) {
        infraService.deleteModuleDatabase(id);
        return "redirect:/infrastructure/modules/" + moduleId + "/edit";
    }

    // --- Database Servers ---
    @GetMapping("/db-servers")
    public String dbServers(@RequestParam(required = false) String search,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             Model model) {
        model.addAttribute("servers", infraService.listServers(search, page, size));
        model.addAttribute("search", search);
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/db-servers";
    }

    @GetMapping("/db-servers/new")
    public String newServer(Model model) {
        model.addAttribute("server", new DatabaseServer());
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/db-server-form";
    }

    @GetMapping("/db-servers/{id}/edit")
    public String editServer(@PathVariable Integer id, Model model) {
        model.addAttribute("server", infraService.findServerById(id));
        model.addAttribute("credentials", infraService.getCredentials(id));
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/db-server-form";
    }

    @PostMapping("/db-servers/save")
    public String saveServer(@ModelAttribute DatabaseServer server) {
        infraService.saveServer(server);
        return "redirect:/infrastructure/db-servers";
    }

    @PostMapping("/db-servers/{id}/delete")
    public String deleteServer(@PathVariable Integer id) {
        infraService.deleteServer(id);
        return "redirect:/infrastructure/db-servers";
    }

    @PostMapping("/db-servers/{serverId}/credentials/save")
    public String saveCredential(@PathVariable Integer serverId,
                                  @ModelAttribute DatabaseCredential credential,
                                  @RequestParam(required = false) String password) {
        credential.setDatabaseServer(infraService.findServerById(serverId));
        infraService.saveCredential(credential, password);
        return "redirect:/infrastructure/db-servers/" + serverId + "/edit";
    }

    @PostMapping("/credentials/{id}/delete")
    public String deleteCredential(@PathVariable Integer id,
                                    @RequestParam Integer serverId) {
        infraService.deleteCredential(id);
        return "redirect:/infrastructure/db-servers/" + serverId + "/edit";
    }
}
