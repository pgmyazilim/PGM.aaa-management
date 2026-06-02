package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ClientModules", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClientModuleId")
    private Integer clientModuleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClientId", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ModuleId", nullable = false)
    private Module module;
}
