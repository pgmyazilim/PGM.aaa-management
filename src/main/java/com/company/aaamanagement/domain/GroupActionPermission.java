package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "GroupActionPermission", schema = "aaa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ActionId", "UserGroupId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupActionPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GroupActionPermissionId")
    private Integer groupActionPermissionId;

    @Column(name = "ModifiedAtUtc", nullable = false)
    private LocalDateTime modifiedAtUtc;

    @Column(name = "CreatedAtUtc", insertable = false, updatable = false)
    private LocalDateTime createdAtUtc;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "RowVersion", insertable = false, updatable = false)
    private byte[] rowVersion;

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

    @Column(name = "UsedExecutionCount", nullable = false)
    private short usedExecutionCount;
}
