package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ActionLogs", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ActionLogId")
    private Long actionLogId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ActionId", nullable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionId")
    private Session session;

    @Column(name = "OccurredAtUtc", nullable = false)
    private LocalDateTime occurredAtUtc;

    @Column(name = "IsSuccess", nullable = false)
    private boolean success;

    @Column(name = "ExtraInfo", columnDefinition = "nvarchar(max)")
    private String extraInfo;

    @Column(name = "UserNote", columnDefinition = "nvarchar(max)")
    private String userNote;
}
