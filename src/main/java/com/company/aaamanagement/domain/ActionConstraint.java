package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ActionConstraints", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class ActionConstraint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ActionConstraintId")
    @ToString.Include
    private Integer actionConstraintId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @Column(name = "Name", nullable = false, length = 200)
    @ToString.Include
    private String name;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "ConstraintKey", nullable = false, length = 400)
    private String constraintKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ActionId", nullable = false)
    private Action action;

    @Enumerated(EnumType.STRING)
    @Column(name = "Operator", length = 40)
    private ConstraintOperator operator;

    @Column(name = "IsOperatorNegated", nullable = false)
    private boolean operatorNegated;
}
