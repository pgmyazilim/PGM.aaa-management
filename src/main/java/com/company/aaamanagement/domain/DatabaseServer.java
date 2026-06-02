package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DatabaseServers", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class DatabaseServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DatabaseServerId")
    @ToString.Include
    private Integer databaseServerId;

    @Column(name = "Version", length = 50)
    private String version;

    @Column(name = "ServerIpAddress", length = 100)
    @ToString.Include
    private String serverIpAddress;

    @Column(name = "ServerHostname", length = 100)
    private String serverHostname;

    @Column(name = "ServerPort", length = 100)
    private String serverPort;

    @Column(name = "IsSqlServer8", nullable = false)
    private boolean sqlServer8;
}
