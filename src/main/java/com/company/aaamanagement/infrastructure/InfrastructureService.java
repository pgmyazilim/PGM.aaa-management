package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.crypto.CryptoService;
import com.company.aaamanagement.domain.*;
import com.company.aaamanagement.domain.Module;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InfrastructureService {

    private final ProjectRepository projectRepository;
    private final ModuleRepository moduleRepository;
    private final DatabaseServerRepository serverRepository;
    private final DatabaseCredentialRepository credentialRepository;
    private final ModuleDatabaseRepository moduleDatabaseRepository;
    private final CryptoService cryptoService;

    // --- Projects ---
    public Page<Project> listProjects(String search, int page, int size) {
        return projectRepository.findBySearch(search, PageRequest.of(page, size, Sort.by("name")));
    }

    public Project findProjectById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proje bulunamadı: " + id));
    }

    @Transactional
    public Project saveProject(Project project) {
        project.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Integer id) {
        projectRepository.deleteById(id);
    }

    // --- Modules ---
    public Page<Module> listModules(Integer projectId, String search, int page, int size) {
        return moduleRepository.findByProjectAndSearch(projectId, search, PageRequest.of(page, size, Sort.by("name")));
    }

    public Module findModuleById(Integer id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modül bulunamadı: " + id));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Module saveModule(Module module) {
        module.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return moduleRepository.save(module);
    }

    @Transactional
    public void deleteModule(Integer id) {
        moduleRepository.deleteById(id);
    }

    // --- Database Servers ---
    public Page<DatabaseServer> listServers(String search, int page, int size) {
        return serverRepository.findBySearch(search, PageRequest.of(page, size));
    }

    public DatabaseServer findServerById(Integer id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sunucu bulunamadı: " + id));
    }

    @Transactional
    public DatabaseServer saveServer(DatabaseServer server) {
        return serverRepository.save(server);
    }

    @Transactional
    public void deleteServer(Integer id) {
        serverRepository.deleteById(id);
    }

    public List<DatabaseCredential> getCredentials(Integer serverId) {
        return credentialRepository.findByDatabaseServer_DatabaseServerId(serverId);
    }

    public List<DatabaseServer> getAllServers() {
        return serverRepository.findAllByOrderByServerHostnameAsc();
    }

    @Transactional
    public DatabaseCredential saveCredential(DatabaseCredential credential, String password) {
        if (password != null && !password.isBlank()) {
            credential.setPasswordEncrypted(cryptoService.encrypt(password));
        }
        return credentialRepository.save(credential);
    }

    @Transactional
    public void deleteCredential(Integer id) {
        credentialRepository.deleteById(id);
    }

    // --- Module Databases ---
    public List<ModuleDatabase> getModuleDatabases(Integer moduleId) {
        return moduleDatabaseRepository.findByModule_ModuleIdOrderByDatabaseNameAsc(moduleId);
    }

    public List<DatabaseCredential> getAllCredentials() {
        return credentialRepository.findAllByOrderByUsernameAsc();
    }

    @Transactional
    public ModuleDatabase addModuleDatabase(Integer moduleId, Integer serverId, Integer credentialId,
                                            String databaseName, String databaseAlias) {
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("Veritabanı adı boş olamaz.");
        }
        Module module = findModuleById(moduleId);
        DatabaseServer server = serverId != null ? findServerById(serverId) : null;
        DatabaseCredential credential = null;
        if (credentialId != null) {
            credential = credentialRepository.findById(credentialId)
                    .orElseThrow(() -> new EntityNotFoundException("Kimlik bilgisi bulunamadı: " + credentialId));
            DatabaseServer credentialServer = credential.getDatabaseServer();
            if (server == null) {
                // sunucu seçilmediyse kimlik bilgisinin sunucusunu kullan
                server = credentialServer;
            } else if (credentialServer != null
                    && !credentialServer.getDatabaseServerId().equals(server.getDatabaseServerId())) {
                throw new IllegalArgumentException("Seçilen kimlik bilgisi seçilen sunucuya ait değil.");
            }
        }
        return moduleDatabaseRepository.save(ModuleDatabase.builder()
                .module(module)
                .databaseServer(server)
                .databaseCredential(credential)
                .databaseName(databaseName.trim())
                .databaseAlias(databaseAlias == null || databaseAlias.isBlank() ? null : databaseAlias.trim())
                .modifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC))
                .build());
    }

    @Transactional
    public void deleteModuleDatabase(Integer id) {
        moduleDatabaseRepository.deleteById(id);
    }
}
