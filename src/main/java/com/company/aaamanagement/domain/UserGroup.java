package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserGroups", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserGroupId")
    @ToString.Include
    private Integer userGroupId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @Column(name = "Name", nullable = false, length = 300, unique = true)
    @ToString.Include
    private String name;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;
}
