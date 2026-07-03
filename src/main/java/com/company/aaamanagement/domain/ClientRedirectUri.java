package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "ClientRedirectUris", schema = "aaa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ClientId", "RedirectUri"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientRedirectUri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClientRedirectUriId")
    private Integer clientRedirectUriId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "RowVersion", insertable = false, updatable = false)
    private byte[] rowVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClientId", nullable = false)
    private Client client;

    @Column(name = "RedirectUri", nullable = false, length = 400)
    private String redirectUri;
}
