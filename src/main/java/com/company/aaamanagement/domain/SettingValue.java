package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SettingValues", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SettingValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SettingValueId")
    private Integer settingValueId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SettingId", nullable = false)
    private Setting setting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserGroupId")
    private UserGroup userGroup;

    @Column(name = "Value", nullable = false, columnDefinition = "nvarchar(max)")
    private String value;
}
