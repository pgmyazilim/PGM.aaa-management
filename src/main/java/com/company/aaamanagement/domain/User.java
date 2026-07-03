package com.company.aaamanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users", schema = "aaa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    @ToString.Include
    private Integer userId;

    @Column(name = "ModifiedAtUtc", nullable = false)
    private LocalDateTime modifiedAtUtc;

    @Column(name = "CreatedAtUtc", insertable = false, updatable = false)
    private LocalDateTime createdAtUtc;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "RowVersion", insertable = false, updatable = false)
    private byte[] rowVersion;

    @Column(name = "FirstName", nullable = false, length = 40)
    private String firstName;

    @Column(name = "MiddleName", length = 40)
    private String middleName;

    @Column(name = "LastName", nullable = false, length = 40)
    private String lastName;

    @Column(name = "Username", nullable = false, length = 40, unique = true)
    @ToString.Include
    private String username;

    @Column(name = "PasswordHashLegacy", length = 64)
    private String passwordHashLegacy;

    @Column(name = "PasswordHash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "FailedLoginCount", nullable = false)
    private int failedLoginCount;

    @Column(name = "LockedUntilUtc")
    private LocalDateTime lockedUntilUtc;

    @Column(name = "OtpSecretEncrypted", length = 256)
    private byte[] otpSecretEncrypted;

    @Column(name = "OtpRecoveryCodesEncrypted", columnDefinition = "varbinary(max)")
    private byte[] otpRecoveryCodesEncrypted;

    @Column(name = "IsActive", nullable = false)
    private boolean active;

    @Column(name = "IsSuperUser", nullable = false)
    private boolean superUser;

    @Column(name = "WelcomeMessage", length = 100)
    private String welcomeMessage;

    @Column(name = "LastPasswordChangeUtc")
    private LocalDateTime lastPasswordChangeUtc;

    @Column(name = "LastSecretChangeUtc")
    private LocalDateTime lastSecretChangeUtc;

    @Column(name = "EmailUser", length = 128)
    private String emailUser;

    @Column(name = "EmailDomain", length = 378)
    private String emailDomain;

    @Column(name = "PhoneHome", length = 25)
    private String phoneHome;

    @Column(name = "PhoneOffice", length = 25)
    private String phoneOffice;

    @Column(name = "PhoneOfficeExt")
    private Integer phoneOfficeExt;

    @Column(name = "PhoneFax", length = 25)
    private String phoneFax;

    @Column(name = "PhoneMobile", length = 25)
    private String phoneMobile;

    @Column(name = "IsPhoneVerified")
    private Boolean phoneVerified;

    @Column(name = "IsEmailVerified")
    private Boolean emailVerified;

    @Column(name = "AlwaysUseOtp")
    private Boolean alwaysUseOtp;

    @Column(name = "BadgeNo", length = 20)
    private String badgeNo;

    @Column(name = "IdNo", length = 20)
    private String idNo;

    public String getFullName() {
        if (middleName != null && !middleName.isBlank()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
}
