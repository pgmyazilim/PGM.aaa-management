package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserGroupMembers", schema = "aaa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"UserId", "UserGroupId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserGroupMemberId")
    private Integer userGroupMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserGroupId", nullable = false)
    private UserGroup userGroup;
}
