package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ActionConstraintGroupValue", schema = "aaa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ActionConstraintId", "UserGroupId"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActionConstraintGroupValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ActionConstraintGroupValueId")
    private Integer actionConstraintGroupValueId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ActionConstraintId", nullable = false)
    private ActionConstraint actionConstraint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserGroupId", nullable = false)
    private UserGroup userGroup;

    @Column(name = "ValueList", nullable = false, columnDefinition = "nvarchar(max)")
    private String valueList;

    @Column(name = "ValueDelimiter", length = 10)
    private String valueDelimiter;

    @Column(name = "ValuesLogicalOperator", length = 6)
    private String valuesLogicalOperator;

    @Column(name = "ValueLogicalOperator", length = 12)
    private String valueLogicalOperator;
}
