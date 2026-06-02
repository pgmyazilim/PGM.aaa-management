package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.ClientModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientModuleRepository extends JpaRepository<ClientModule, Integer> {

    List<ClientModule> findByClient_ClientId(Integer clientId);

    boolean existsByClient_ClientIdAndModule_ModuleId(Integer clientId, Integer moduleId);

    void deleteByClient_ClientIdAndModule_ModuleId(Integer clientId, Integer moduleId);
}
