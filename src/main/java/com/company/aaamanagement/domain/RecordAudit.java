package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RecordAudits", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecordAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordAuditId")
    private Long recordAuditId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TrackedTableId", nullable = false)
    private TrackedTable trackedTable;

    @Column(name = "RecordId", nullable = false, length = 100)
    private String recordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "OperationType", nullable = false, columnDefinition = "nchar(1)")
    private OperationType operationType;

    @Column(name = "OccurredAtUtc", nullable = false)
    private LocalDateTime occurredAtUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ActorUserId", nullable = false)
    private User actorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionId")
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OperationLogId")
    private ActionLog operationLog;

    @Column(name = "RecordValues", columnDefinition = "nvarchar(max)")
    private String recordValues;

    @Column(name = "ExtraInfo", columnDefinition = "nvarchar(max)")
    private String extraInfo;
}
