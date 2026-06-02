package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Projects", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectId")
    @ToString.Include
    private Integer projectId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @Column(name = "Name", length = 500)
    @ToString.Include
    private String name;
}
