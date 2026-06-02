package com.company.aaamanagement.infrastructure;

import com.company.aaamanagement.domain.DatabaseCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatabaseCredentialRepository extends JpaRepository<DatabaseCredential, Integer> {

    List<DatabaseCredential> findByDatabaseServer_DatabaseServerId(Integer serverId);
}
