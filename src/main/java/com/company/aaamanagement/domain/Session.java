package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Sessions", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SessionId")
    @ToString.Include
    private Long sessionId;

    @Column(name = "RowVersionUtc", nullable = false)
    private LocalDateTime rowVersionUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @Column(name = "SessionKey", nullable = false, unique = true, columnDefinition = "uniqueidentifier")
    private UUID sessionKey;

    @Column(name = "IsOpen", nullable = false)
    private boolean open;

    @Column(name = "OpenedAtUtc", nullable = false)
    private LocalDateTime openedAtUtc;

    @Column(name = "ClosedAtUtc")
    private LocalDateTime closedAtUtc;

    @Column(name = "IsNormalClose")
    private Boolean normalClose;

    @Column(name = "ClientIpAddress", length = 78)
    private String clientIpAddress;

    @Column(name = "ClientMacAddress", length = 34)
    private String clientMacAddress;

    @Column(name = "ExtraInfo", columnDefinition = "nvarchar(max)")
    private String extraInfo;
}
