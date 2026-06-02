package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TrackedTables", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class TrackedTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TrackedTableId")
    @ToString.Include
    private Integer trackedTableId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @Column(name = "Name", nullable = false, length = 240, unique = true)
    @ToString.Include
    private String name;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "ActorTrackingTypes", length = 8)
    private String actorTrackingTypes;

    @Column(name = "RecordTrackingTypes", length = 8)
    private String recordTrackingTypes;
}
