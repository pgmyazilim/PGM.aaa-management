package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GroupActionPermission", schema = "aaa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ActionId", "UserGroupId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupActionPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GroupOperationPermissionId")
    private Integer groupOperationPermissionId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ActionId", nullable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserGroupId", nullable = false)
    private UserGroup userGroup;

    @Column(name = "IsAllowed", nullable = false)
    private boolean allowed;

    @Column(name = "ExpiresAtUtc")
    private LocalDateTime expiresAtUtc;

    @Column(name = "AllowedExecutionCount")
    private Short allowedExecutionCount;
}
