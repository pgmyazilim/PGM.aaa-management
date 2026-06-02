package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DbInfo", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DbInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DbInfoId")
    private Integer dbInfoId;

    @Column(name = "VersionUtc", nullable = false, unique = true)
    private LocalDateTime versionUtc;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;
}
