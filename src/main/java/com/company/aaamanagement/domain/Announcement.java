package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "Announcements", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AnnouncementId")
    @ToString.Include
    private Integer announcementId;

    @Column(name = "ModifiedAtUtc", nullable = false)
    private LocalDateTime modifiedAtUtc;

    @Column(name = "CreatedAtUtc", insertable = false, updatable = false)
    private LocalDateTime createdAtUtc;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "RowVersion", insertable = false, updatable = false)
    private byte[] rowVersion;

    @Column(name = "Title", nullable = false, length = 300)
    @ToString.Include
    private String title;

    @Column(name = "Body", nullable = false, columnDefinition = "nvarchar(max)")
    private String body;

    @Column(name = "BodyFormat", nullable = false, length = 10)
    @Builder.Default
    private String bodyFormat = "markdown";

    @Column(name = "Severity", nullable = false, length = 12)
    @Builder.Default
    private String severity = "info";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "PublishFromUtc", nullable = false)
    private LocalDateTime publishFromUtc;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "PublishUntilUtc")
    private LocalDateTime publishUntilUtc;

    @Column(name = "IsActive", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "IsGlobal", nullable = false)
    private boolean global;

    @Column(name = "IsPinned", nullable = false)
    private boolean pinned;

    @Column(name = "RequiresAcknowledgement", nullable = false)
    private boolean requiresAcknowledgement;

    @Column(name = "IsDismissible", nullable = false)
    @Builder.Default
    private boolean dismissible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedByUserId", nullable = false)
    private User createdByUser;

    @Column(name = "ExtraInfo", columnDefinition = "nvarchar(max)")
    private String extraInfo;
}
