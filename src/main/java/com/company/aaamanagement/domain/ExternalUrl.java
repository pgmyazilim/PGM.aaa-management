package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "ExternalUrls", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class ExternalUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ExternalUrlId")
    @ToString.Include
    private Integer externalUrlId;

    @Column(name = "ModifiedAtUtc", nullable = false)
    private LocalDateTime modifiedAtUtc;

    @Column(name = "CreatedAtUtc", insertable = false, updatable = false)
    private LocalDateTime createdAtUtc;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "RowVersion", insertable = false, updatable = false)
    private byte[] rowVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProjectId", nullable = false)
    private Project project;

    @Column(name = "Name", nullable = false, length = 100)
    @ToString.Include
    private String name;

    @Column(name = "Url", nullable = false, length = 2048)
    private String url;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;
}
