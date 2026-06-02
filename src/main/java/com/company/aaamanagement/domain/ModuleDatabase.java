package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ModulesDatabases", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModuleDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ModuleDatabaseId")
    private Integer moduleDatabaseId;

    @Column(name = "RowVersionUtc")
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ModuleId")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DatabaseServerId")
    private DatabaseServer databaseServer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DatabaseCredentialId")
    private DatabaseCredential databaseCredential;

    @Column(name = "DatabaseName", nullable = false, length = 200)
    private String databaseName;

    @Column(name = "DatabaseAlias", length = 100)
    private String databaseAlias;
}
