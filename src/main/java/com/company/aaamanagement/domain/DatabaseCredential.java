package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DatabaseCredentials", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DatabaseCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DatabaseCredentialId")
    private Integer databaseCredentialId;

    @Column(name = "Version", length = 50)
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DatabaseServerId")
    private DatabaseServer databaseServer;

    @Column(name = "Username", length = 100)
    private String username;

    @Column(name = "Password", length = 100)
    private String password;
}
