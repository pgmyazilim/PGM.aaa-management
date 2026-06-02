package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Modules", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ModuleId")
    @ToString.Include
    private Integer moduleId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectId", nullable = false)
    private Project project;

    @Column(name = "Name", length = 200)
    @ToString.Include
    private String name;
}
