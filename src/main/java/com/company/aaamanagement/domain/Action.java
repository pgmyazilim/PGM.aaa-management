package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Actions", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ActionId")
    @ToString.Include
    private Integer actionId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ModuleId", nullable = false)
    private Module module;

    @Column(name = "Name", nullable = false, length = 200)
    @ToString.Include
    private String name;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "ActionKey", nullable = false, length = 200, unique = true)
    private String actionKey;

    @Column(name = "IsActive", nullable = false)
    private boolean active;

    @Column(name = "RequiresAuthorization", nullable = false)
    private boolean requiresAuthorization;

    @Column(name = "LogOnSuccess", nullable = false)
    private boolean logOnSuccess;

    @Column(name = "LogOnFailure", nullable = false)
    private boolean logOnFailure;
}
