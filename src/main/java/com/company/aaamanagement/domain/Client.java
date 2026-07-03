package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "Clients", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClientId")
    @ToString.Include
    private Integer clientId;

    @Column(name = "ModifiedAtUtc", nullable = false)
    private LocalDateTime modifiedAtUtc;

    @Column(name = "CreatedAtUtc", insertable = false, updatable = false)
    private LocalDateTime createdAtUtc;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "RowVersion", insertable = false, updatable = false)
    private byte[] rowVersion;

    @Column(name = "Name", nullable = false, length = 200, unique = true)
    @ToString.Include
    private String name;

    @Column(name = "ClientSecretHash", length = 200)
    private String clientSecretHash;

    @Column(name = "IsActive", nullable = false)
    private boolean active;

    @Column(name = "AllowedGrantTypes", length = 200)
    private String allowedGrantTypes;

    @Column(name = "AccessTokenLifetimeSeconds", nullable = false)
    @Builder.Default
    private int accessTokenLifetimeSeconds = 3600;

    @Column(name = "RefreshTokenLifetimeSeconds", nullable = false)
    @Builder.Default
    private int refreshTokenLifetimeSeconds = 1209600;
}
