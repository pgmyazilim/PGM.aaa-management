package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.*;
import com.company.aaamanagement.domain.Module;
import lombok.RequiredArgsConstructor;
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
    public String projects(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "20") int size,
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
    public String editProject(@PathVariable("id") Integer id, Model model) {
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
    public String deleteProject(@PathVariable("id") Integer id) {
        infraService.deleteProject(id);
        return "redirect:/infrastructure/projects";
    }

    // --- Modules ---
    @GetMapping("/modules")
    public String modules(@RequestParam(value = "projectId", required = false) Integer projectId,
                           @RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "20") int size,
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
    public String editModule(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("module", infraService.findModuleById(id));
        model.addAttribute("projects", infraService.getAllProjects());
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/module-form";
    }

    @PostMapping("/modules/save")
    public String saveModule(@ModelAttribute Module module) {
        infraService.saveModule(module);
        return "redirect:/infrastructure/modules";
    }

    @PostMapping("/modules/{id}/delete")
    public String deleteModule(@PathVariable("id") Integer id) {
        infraService.deleteModule(id);
        return "redirect:/infrastructure/modules";
    }

    // --- Databases ---
    @GetMapping("/databases")
    public String databases(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "20") int size,
                            Model model) {
        model.addAttribute("databases", infraService.listDatabases(search, page, size));
        model.addAttribute("search", search);
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/databases";
    }

    @GetMapping("/databases/new")
    public String newDatabase(Model model) {
        model.addAttribute("database", new ModuleDatabase());
        model.addAttribute("servers", infraService.getAllServers());
        model.addAttribute("credentials", infraService.getAllCredentials());
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/database-form";
    }

    @GetMapping("/databases/{id}/edit")
    public String editDatabase(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("database", infraService.findDatabaseById(id));
        model.addAttribute("servers", infraService.getAllServers());
        model.addAttribute("credentials", infraService.getAllCredentials());
        model.addAttribute("activePage", "infrastructure");
        return "infrastructure/database-form";
    }

    @PostMapping("/databases/save")
    public String saveDatabase(@RequestParam(value = "moduleDatabaseId", required = false) Integer moduleDatabaseId,
                               @RequestParam(value = "databaseServerId", required = false) Integer databaseServerId,
                               @RequestParam(value = "databaseCredentialId", required = false) Integer databaseCredentialId,
                               @RequestParam("databaseName") String databaseName,
                               @RequestParam(value = "databaseAlias", required = false) String databaseAlias) {
        infraService.saveDatabase(moduleDatabaseId, databaseServerId, databaseCredentialId,
                databaseName, databaseAlias);
        return "redirect:/infrastructure/databases";
    }

    @PostMapping("/databases/{id}/delete")
    public String deleteDatabase(@PathVariable("id") Integer id) {
        infraService.deleteDatabase(id);
        return "redirect:/infrastructure/databases";
    }

    // --- Database Servers ---
    @GetMapping("/db-servers")
    public String dbServers(@RequestParam(value = "search", required = false) String search,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "20") int size,
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
    public String editServer(@PathVariable("id") Integer id, Model model) {
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
    public String deleteServer(@PathVariable("id") Integer id) {
        infraService.deleteServer(id);
        return "redirect:/infrastructure/db-servers";
    }

    @PostMapping("/db-servers/{serverId}/credentials/save")
    public String saveCredential(@PathVariable("serverId") Integer serverId,
                                  @ModelAttribute DatabaseCredential credential,
                                  @RequestParam(value = "password", required = false) String password) {
        credential.setDatabaseServer(infraService.findServerById(serverId));
        infraService.saveCredential(credential, password);
        return "redirect:/infrastructure/db-servers/" + serverId + "/edit";
    }

    @PostMapping("/credentials/{id}/delete")
    public String deleteCredential(@PathVariable("id") Integer id,
                                    @RequestParam("serverId") Integer serverId) {
        infraService.deleteCredential(id);
        return "redirect:/infrastructure/db-servers/" + serverId + "/edit";
    }
}
