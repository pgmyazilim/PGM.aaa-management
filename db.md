USE [AAA]
GO
/****** Object:  Table [aaa].[ActionConstraintGroupValue]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[ActionConstraintGroupValue](
[ActionConstraintGroupValueId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[ActionConstraintId] [int] NOT NULL,
[UserGroupId] [int] NOT NULL,
[ValueList] [nvarchar](max) NOT NULL,
[ValueDelimiter] [nvarchar](10) NULL,
[ValuesLogicalOperator] [nvarchar](6) NULL,
[ValueLogicalOperator] [nvarchar](12) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_ActionConstraintGroupValue] PRIMARY KEY CLUSTERED
(
[ActionConstraintGroupValueId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_ActionConstraintGroupValue] UNIQUE NONCLUSTERED
(
[ActionConstraintId] ASC,
[UserGroupId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[ActionConstraints]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[ActionConstraints](
[ActionConstraintId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[Name] [nvarchar](200) NOT NULL,
[Description] [nvarchar](max) NULL,
[ConstraintKey] [nvarchar](400) NOT NULL,
[ActionId] [int] NOT NULL,
[Operator] [nvarchar](40) NULL,
[IsOperatorNegated] [bit] NOT NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_ActionConstraints] PRIMARY KEY CLUSTERED
(
[ActionConstraintId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_ActionConstraints_ConstraintKey_Action] UNIQUE NONCLUSTERED
(
[ConstraintKey] ASC,
[ActionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_ActionConstraints_Name_Action] UNIQUE NONCLUSTERED
(
[Name] ASC,
[ActionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[ActionLogs]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[ActionLogs](
[ActionLogId] [bigint] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[ActionId] [int] NOT NULL,
[SessionId] [bigint] NULL,
[OccurredAtUtc] [datetime2](7) NOT NULL,
[IsSuccess] [bit] NOT NULL,
[ExtraInfo] [nvarchar](max) NULL,
[UserNote] [nvarchar](max) NULL,
[RowVersion] [timestamp] NOT NULL,
[ActorUserId] [int] NULL,
CONSTRAINT [PK_ActionLogs] PRIMARY KEY CLUSTERED
(
[ActionLogId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[Actions]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[Actions](
[ActionId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[ModuleId] [int] NOT NULL,
[Name] [nvarchar](200) NOT NULL,
[Description] [nvarchar](max) NULL,
[ActionKey] [nvarchar](200) NOT NULL,
[IsActive] [bit] NOT NULL,
[RequiresAuthorization] [bit] NOT NULL,
[LogOnSuccess] [bit] NOT NULL,
[LogOnFailure] [bit] NOT NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_Actions] PRIMARY KEY CLUSTERED
(
[ActionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_Actions_ActionKey] UNIQUE NONCLUSTERED
(
[ActionKey] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_Actions_ModuleId_Name] UNIQUE NONCLUSTERED
(
[ModuleId] ASC,
[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[ClientModules]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[ClientModules](
[ClientModuleId] [int] IDENTITY(1,1) NOT NULL,
[ClientId] [int] NOT NULL,
[ModuleId] [int] NOT NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_ClientModules] PRIMARY KEY CLUSTERED
(
[ClientModuleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_ClientModules_Client_Module] UNIQUE NONCLUSTERED
(
[ClientId] ASC,
[ModuleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[ClientRedirectUris]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[ClientRedirectUris](
[ClientRedirectUriId] [int] IDENTITY(1,1) NOT NULL,
[ClientId] [int] NOT NULL,
[RedirectUri] [nvarchar](400) NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_ClientRedirectUris] PRIMARY KEY CLUSTERED
(
[ClientRedirectUriId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_ClientRedirectUris_Client_Uri] UNIQUE NONCLUSTERED
(
[ClientId] ASC,
[RedirectUri] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[Clients]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[Clients](
[ClientId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[Name] [nvarchar](200) NOT NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
[ClientSecretHash] [nvarchar](200) NULL,
[IsActive] [bit] NOT NULL,
[AllowedGrantTypes] [nvarchar](200) NULL,
[AccessTokenLifetimeSeconds] [int] NOT NULL,
[RefreshTokenLifetimeSeconds] [int] NOT NULL,
CONSTRAINT [PK_Clients] PRIMARY KEY CLUSTERED
(
[ClientId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_Clients_Name] UNIQUE NONCLUSTERED
(
[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[DatabaseCredentials]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[DatabaseCredentials](
[DatabaseCredentialId] [int] IDENTITY(1,1) NOT NULL,
[Version] [nvarchar](50) NULL,
[DatabaseServerId] [int] NULL,
[Username] [nvarchar](100) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
[PasswordEncrypted] [varbinary](512) NULL,
CONSTRAINT [PK_DatabaseCredential] PRIMARY KEY CLUSTERED
(
[DatabaseCredentialId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[DatabaseServers]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[DatabaseServers](
[DatabaseServerId] [int] IDENTITY(1,1) NOT NULL,
[Version] [nvarchar](50) NULL,
[ServerIpAddress] [nvarchar](100) NULL,
[ServerHostname] [nvarchar](100) NULL,
[ServerPort] [nvarchar](100) NULL,
[IsSqlServer8] [bit] NOT NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_DatabaseServer] PRIMARY KEY CLUSTERED
(
[DatabaseServerId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[DbInfo]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[DbInfo](
[DbInfoId] [int] IDENTITY(1,1) NOT NULL,
[VersionUtc] [datetime2](7) NOT NULL,
[Description] [nvarchar](max) NULL,
CONSTRAINT [PK_DbInfo] PRIMARY KEY CLUSTERED
(
[DbInfoId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_DbInfo_VersionUtc] UNIQUE NONCLUSTERED
(
[VersionUtc] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[GroupActionPermission]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[GroupActionPermission](
[GroupActionPermissionId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[ActionId] [int] NOT NULL,
[UserGroupId] [int] NOT NULL,
[IsAllowed] [bit] NOT NULL,
[ExpiresAtUtc] [datetime2](7) NULL,
[AllowedExecutionCount] [smallint] NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
[UsedExecutionCount] [smallint] NOT NULL,
CONSTRAINT [PK_GroupActionPermission] PRIMARY KEY CLUSTERED
(
[GroupActionPermissionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_GroupActionPermission_Action_Group] UNIQUE NONCLUSTERED
(
[ActionId] ASC,
[UserGroupId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[Modules]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[Modules](
[ModuleId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[ProjectId] [int] NOT NULL,
[Name] [nvarchar](200) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_Module] PRIMARY KEY CLUSTERED
(
[ModuleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[ModulesDatabases]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[ModulesDatabases](
[ModuleDatabaseId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NULL,
[ModuleId] [int] NOT NULL,
[DatabaseServerId] [int] NULL,
[DatabaseCredentialId] [int] NULL,
[DatabaseName] [nvarchar](200) NOT NULL,
[DatabaseAlias] [nvarchar](100) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_ModuleDatabase] PRIMARY KEY CLUSTERED
(
[ModuleDatabaseId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[Projects]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[Projects](
[ProjectId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[Name] [nvarchar](500) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_Project] PRIMARY KEY CLUSTERED
(
[ProjectId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[RecordAudits]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[RecordAudits](
[RecordAuditId] [bigint] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[TrackedTableId] [int] NOT NULL,
[RecordId] [nvarchar](100) NOT NULL,
[OperationType] [nchar](1) NOT NULL,
[OccurredAtUtc] [datetime2](7) NOT NULL,
[ActorUserId] [int] NOT NULL,
[SessionId] [bigint] NULL,
[ActionLogId] [bigint] NULL,
[RecordValues] [nvarchar](max) NULL,
[ExtraInfo] [nvarchar](max) NULL,
[RowVersion] [timestamp] NOT NULL,
CONSTRAINT [PK_RecordAudit] PRIMARY KEY CLUSTERED
(
[RecordAuditId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[Sessions]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[Sessions](
[SessionId] [bigint] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[UserId] [int] NOT NULL,
[SessionKey] [uniqueidentifier] NOT NULL,
[IsOpen] [bit] NOT NULL,
[OpenedAtUtc] [datetime2](7) NOT NULL,
[ClosedAtUtc] [datetime2](7) NULL,
[IsNormalClose] [bit] NULL,
[ClientIpAddress] [nvarchar](78) NULL,
[ClientMacAddress] [nvarchar](34) NULL,
[ExtraInfo] [nvarchar](max) NULL,
[RowVersion] [timestamp] NOT NULL,
[ClientId] [int] NULL,
[ExpiresAtUtc] [datetime2](7) NULL,
[LastActivityUtc] [datetime2](7) NULL,
CONSTRAINT [PK_Session] PRIMARY KEY CLUSTERED
(
[SessionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_Session_SessionKey] UNIQUE NONCLUSTERED
(
[SessionKey] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[Settings]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[Settings](
[SettingId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[Description] [nvarchar](max) NULL,
[SettingKey] [nvarchar](200) NOT NULL,
[DefaultValue] [nvarchar](max) NOT NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_Setting] PRIMARY KEY CLUSTERED
(
[SettingId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_Setting_SettingKey] UNIQUE NONCLUSTERED
(
[SettingKey] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[SettingValues]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[SettingValues](
[SettingValueId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[SettingId] [int] NOT NULL,
[UserId] [int] NULL,
[UserGroupId] [int] NULL,
[Value] [nvarchar](max) NOT NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_SettingValue] PRIMARY KEY CLUSTERED
(
[SettingValueId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[TrackedTables]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[TrackedTables](
[TrackedTableId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[Name] [nvarchar](240) NOT NULL,
[Description] [nvarchar](max) NULL,
[ActorTrackingTypes] [nvarchar](8) NULL,
[RecordTrackingTypes] [nvarchar](8) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_TrackedTable] PRIMARY KEY CLUSTERED
(
[TrackedTableId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_TrackedTable_Name] UNIQUE NONCLUSTERED
(
[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[UserGroupMembers]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[UserGroupMembers](
[UserGroupMemberId] [int] IDENTITY(1,1) NOT NULL,
[UserId] [int] NOT NULL,
[UserGroupId] [int] NOT NULL,
CONSTRAINT [PK_UserGroupMember] PRIMARY KEY CLUSTERED
(
[UserGroupMemberId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_UserGroupMember_User_Group] UNIQUE NONCLUSTERED
(
[UserId] ASC,
[UserGroupId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [aaa].[UserGroups]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[UserGroups](
[UserGroupId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[Name] [nvarchar](300) NOT NULL,
[Description] [nvarchar](max) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
CONSTRAINT [PK_UserGroup] PRIMARY KEY CLUSTERED
(
[UserGroupId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_UserGroup_Name] UNIQUE NONCLUSTERED
(
[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [aaa].[Users]    Script Date: 04/07/2026 10:38:24 am ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [aaa].[Users](
[UserId] [int] IDENTITY(1,1) NOT NULL,
[ModifiedAtUtc] [datetime2](7) NOT NULL,
[FirstName] [nvarchar](40) NOT NULL,
[MiddleName] [nvarchar](40) NULL,
[LastName] [nvarchar](40) NOT NULL,
[Username] [nvarchar](40) NOT NULL,
[PasswordHashLegacy] [nvarchar](64) NULL,
[PasswordHash] [nvarchar](200) NOT NULL,
[IsActive] [bit] NOT NULL,
[IsSuperUser] [bit] NOT NULL,
[WelcomeMessage] [nvarchar](100) NULL,
[LastPasswordChangeUtc] [datetime2](7) NULL,
[LastSecretChangeUtc] [datetime2](7) NULL,
[EmailUser] [nvarchar](128) NULL,
[EmailDomain] [nvarchar](378) NULL,
[PhoneHome] [nvarchar](25) NULL,
[PhoneOffice] [nvarchar](25) NULL,
[PhoneOfficeExt] [int] NULL,
[PhoneFax] [nvarchar](25) NULL,
[PhoneMobile] [nvarchar](25) NULL,
[IsPhoneVerified] [bit] NULL,
[IsEmailVerified] [bit] NULL,
[AlwaysUseOtp] [bit] NULL,
[BadgeNo] [nvarchar](20) NULL,
[IdNo] [nvarchar](20) NULL,
[RowVersion] [timestamp] NOT NULL,
[CreatedAtUtc] [datetime2](7) NOT NULL,
[OtpSecretEncrypted] [varbinary](256) NULL,
[OtpRecoveryCodesEncrypted] [varbinary](max) NULL,
[FailedLoginCount] [int] NOT NULL,
[LockedUntilUtc] [datetime2](7) NULL,
CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED
(
[UserId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
CONSTRAINT [UQ_User_Username] UNIQUE NONCLUSTERED
(
[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue] ADD  CONSTRAINT [DF_ActionConstraintGroupValue_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[ActionConstraints] ADD  CONSTRAINT [DF_ActionConstraints_Operator]  DEFAULT ('equals') FOR [Operator]
GO
ALTER TABLE [aaa].[ActionConstraints] ADD  CONSTRAINT [DF_ActionConstraints_IsOperatorNegated]  DEFAULT ((0)) FOR [IsOperatorNegated]
GO
ALTER TABLE [aaa].[ActionConstraints] ADD  CONSTRAINT [DF_ActionConstraints_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[ActionLogs] ADD  CONSTRAINT [DF_ActionLogs_OccurredAtUtc]  DEFAULT (sysutcdatetime()) FOR [OccurredAtUtc]
GO
ALTER TABLE [aaa].[Actions] ADD  CONSTRAINT [DF_Actions_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[ClientModules] ADD  CONSTRAINT [DF_ClientModules_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[ClientRedirectUris] ADD  CONSTRAINT [DF_ClientRedirectUris_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[Clients] ADD  CONSTRAINT [DF_Clients_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[Clients] ADD  CONSTRAINT [DF_Clients_IsActive]  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [aaa].[Clients] ADD  CONSTRAINT [DF_Clients_AccessTokenLifetime]  DEFAULT ((3600)) FOR [AccessTokenLifetimeSeconds]
GO
ALTER TABLE [aaa].[Clients] ADD  CONSTRAINT [DF_Clients_RefreshTokenLifetime]  DEFAULT ((1209600)) FOR [RefreshTokenLifetimeSeconds]
GO
ALTER TABLE [aaa].[DatabaseCredentials] ADD  CONSTRAINT [DF_DatabaseCredentials_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[DatabaseServers] ADD  CONSTRAINT [DF_DatabaseServers_IsSqlServer8]  DEFAULT ((0)) FOR [IsSqlServer8]
GO
ALTER TABLE [aaa].[DatabaseServers] ADD  CONSTRAINT [DF_DatabaseServers_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[GroupActionPermission] ADD  CONSTRAINT [DF_GroupActionPermission_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[GroupActionPermission] ADD  CONSTRAINT [DF_GroupActionPermission_UsedCount]  DEFAULT ((0)) FOR [UsedExecutionCount]
GO
ALTER TABLE [aaa].[Modules] ADD  CONSTRAINT [DF_Modules_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[ModulesDatabases] ADD  CONSTRAINT [DF_ModulesDatabases_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[Projects] ADD  CONSTRAINT [DF_Projects_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[RecordAudits] ADD  CONSTRAINT [DF_RecordAudit_OccurredAtUtc]  DEFAULT (sysutcdatetime()) FOR [OccurredAtUtc]
GO
ALTER TABLE [aaa].[Sessions] ADD  CONSTRAINT [DF_Sessions_SessionKey]  DEFAULT (newid()) FOR [SessionKey]
GO
ALTER TABLE [aaa].[Sessions] ADD  CONSTRAINT [DF_Sessions_IsOpen]  DEFAULT ((1)) FOR [IsOpen]
GO
ALTER TABLE [aaa].[Sessions] ADD  CONSTRAINT [DF_Session_OpenedAtUtc]  DEFAULT (sysutcdatetime()) FOR [OpenedAtUtc]
GO
ALTER TABLE [aaa].[Settings] ADD  CONSTRAINT [DF_Settings_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[SettingValues] ADD  CONSTRAINT [DF_SettingValues_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[TrackedTables] ADD  CONSTRAINT [DF_TrackedTables_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[UserGroups] ADD  CONSTRAINT [DF_UserGroups_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[Users] ADD  CONSTRAINT [DF_Users_CreatedAtUtc]  DEFAULT (sysutcdatetime()) FOR [CreatedAtUtc]
GO
ALTER TABLE [aaa].[Users] ADD  CONSTRAINT [DF_Users_FailedLoginCount]  DEFAULT ((0)) FOR [FailedLoginCount]
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue]  WITH CHECK ADD  CONSTRAINT [FK_ActionConstraintGroupValue_Constraint] FOREIGN KEY([ActionConstraintId])
REFERENCES [aaa].[ActionConstraints] ([ActionConstraintId])
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue] CHECK CONSTRAINT [FK_ActionConstraintGroupValue_Constraint]
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue]  WITH CHECK ADD  CONSTRAINT [FK_ActionConstraintGroupValue_Group] FOREIGN KEY([UserGroupId])
REFERENCES [aaa].[UserGroups] ([UserGroupId])
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue] CHECK CONSTRAINT [FK_ActionConstraintGroupValue_Group]
GO
ALTER TABLE [aaa].[ActionConstraints]  WITH CHECK ADD  CONSTRAINT [FK_ActionConstraints_Action] FOREIGN KEY([ActionId])
REFERENCES [aaa].[Actions] ([ActionId])
GO
ALTER TABLE [aaa].[ActionConstraints] CHECK CONSTRAINT [FK_ActionConstraints_Action]
GO
ALTER TABLE [aaa].[ActionLogs]  WITH CHECK ADD  CONSTRAINT [FK_ActionLogs_Action] FOREIGN KEY([ActionId])
REFERENCES [aaa].[Actions] ([ActionId])
GO
ALTER TABLE [aaa].[ActionLogs] CHECK CONSTRAINT [FK_ActionLogs_Action]
GO
ALTER TABLE [aaa].[ActionLogs]  WITH CHECK ADD  CONSTRAINT [FK_ActionLogs_ActorUser] FOREIGN KEY([ActorUserId])
REFERENCES [aaa].[Users] ([UserId])
GO
ALTER TABLE [aaa].[ActionLogs] CHECK CONSTRAINT [FK_ActionLogs_ActorUser]
GO
ALTER TABLE [aaa].[ActionLogs]  WITH CHECK ADD  CONSTRAINT [FK_ActionLogs_Session] FOREIGN KEY([SessionId])
REFERENCES [aaa].[Sessions] ([SessionId])
GO
ALTER TABLE [aaa].[ActionLogs] CHECK CONSTRAINT [FK_ActionLogs_Session]
GO
ALTER TABLE [aaa].[Actions]  WITH CHECK ADD  CONSTRAINT [FK_Actions_Module] FOREIGN KEY([ModuleId])
REFERENCES [aaa].[Modules] ([ModuleId])
GO
ALTER TABLE [aaa].[Actions] CHECK CONSTRAINT [FK_Actions_Module]
GO
ALTER TABLE [aaa].[ClientModules]  WITH CHECK ADD  CONSTRAINT [FK_ClientModules_Client] FOREIGN KEY([ClientId])
REFERENCES [aaa].[Clients] ([ClientId])
GO
ALTER TABLE [aaa].[ClientModules] CHECK CONSTRAINT [FK_ClientModules_Client]
GO
ALTER TABLE [aaa].[ClientModules]  WITH CHECK ADD  CONSTRAINT [FK_ClientModules_Module] FOREIGN KEY([ModuleId])
REFERENCES [aaa].[Modules] ([ModuleId])
GO
ALTER TABLE [aaa].[ClientModules] CHECK CONSTRAINT [FK_ClientModules_Module]
GO
ALTER TABLE [aaa].[ClientRedirectUris]  WITH CHECK ADD  CONSTRAINT [FK_ClientRedirectUris_Client] FOREIGN KEY([ClientId])
REFERENCES [aaa].[Clients] ([ClientId])
GO
ALTER TABLE [aaa].[ClientRedirectUris] CHECK CONSTRAINT [FK_ClientRedirectUris_Client]
GO
ALTER TABLE [aaa].[DatabaseCredentials]  WITH CHECK ADD  CONSTRAINT [FK_DatabaseCredential_DatabaseServer] FOREIGN KEY([DatabaseServerId])
REFERENCES [aaa].[DatabaseServers] ([DatabaseServerId])
GO
ALTER TABLE [aaa].[DatabaseCredentials] CHECK CONSTRAINT [FK_DatabaseCredential_DatabaseServer]
GO
ALTER TABLE [aaa].[GroupActionPermission]  WITH CHECK ADD  CONSTRAINT [FK_GroupActionPermission_Action] FOREIGN KEY([ActionId])
REFERENCES [aaa].[Actions] ([ActionId])
GO
ALTER TABLE [aaa].[GroupActionPermission] CHECK CONSTRAINT [FK_GroupActionPermission_Action]
GO
ALTER TABLE [aaa].[GroupActionPermission]  WITH CHECK ADD  CONSTRAINT [FK_GroupActionPermission_Group] FOREIGN KEY([UserGroupId])
REFERENCES [aaa].[UserGroups] ([UserGroupId])
GO
ALTER TABLE [aaa].[GroupActionPermission] CHECK CONSTRAINT [FK_GroupActionPermission_Group]
GO
ALTER TABLE [aaa].[Modules]  WITH CHECK ADD  CONSTRAINT [FK_Module_Project] FOREIGN KEY([ProjectId])
REFERENCES [aaa].[Projects] ([ProjectId])
GO
ALTER TABLE [aaa].[Modules] CHECK CONSTRAINT [FK_Module_Project]
GO
ALTER TABLE [aaa].[ModulesDatabases]  WITH CHECK ADD  CONSTRAINT [FK_ModuleDatabase_DatabaseCredential] FOREIGN KEY([DatabaseCredentialId])
REFERENCES [aaa].[DatabaseCredentials] ([DatabaseCredentialId])
GO
ALTER TABLE [aaa].[ModulesDatabases] CHECK CONSTRAINT [FK_ModuleDatabase_DatabaseCredential]
GO
ALTER TABLE [aaa].[ModulesDatabases]  WITH CHECK ADD  CONSTRAINT [FK_ModuleDatabase_DatabaseServer] FOREIGN KEY([DatabaseServerId])
REFERENCES [aaa].[DatabaseServers] ([DatabaseServerId])
GO
ALTER TABLE [aaa].[ModulesDatabases] CHECK CONSTRAINT [FK_ModuleDatabase_DatabaseServer]
GO
ALTER TABLE [aaa].[ModulesDatabases]  WITH CHECK ADD  CONSTRAINT [FK_ModuleDatabase_Module] FOREIGN KEY([ModuleId])
REFERENCES [aaa].[Modules] ([ModuleId])
GO
ALTER TABLE [aaa].[ModulesDatabases] CHECK CONSTRAINT [FK_ModuleDatabase_Module]
GO
ALTER TABLE [aaa].[RecordAudits]  WITH CHECK ADD  CONSTRAINT [FK_RecordAudit_ActionLog] FOREIGN KEY([ActionLogId])
REFERENCES [aaa].[ActionLogs] ([ActionLogId])
GO
ALTER TABLE [aaa].[RecordAudits] CHECK CONSTRAINT [FK_RecordAudit_ActionLog]
GO
ALTER TABLE [aaa].[RecordAudits]  WITH CHECK ADD  CONSTRAINT [FK_RecordAudit_ActorUser] FOREIGN KEY([ActorUserId])
REFERENCES [aaa].[Users] ([UserId])
GO
ALTER TABLE [aaa].[RecordAudits] CHECK CONSTRAINT [FK_RecordAudit_ActorUser]
GO
ALTER TABLE [aaa].[RecordAudits]  WITH CHECK ADD  CONSTRAINT [FK_RecordAudit_Session] FOREIGN KEY([SessionId])
REFERENCES [aaa].[Sessions] ([SessionId])
GO
ALTER TABLE [aaa].[RecordAudits] CHECK CONSTRAINT [FK_RecordAudit_Session]
GO
ALTER TABLE [aaa].[RecordAudits]  WITH CHECK ADD  CONSTRAINT [FK_RecordAudit_TrackedTable] FOREIGN KEY([TrackedTableId])
REFERENCES [aaa].[TrackedTables] ([TrackedTableId])
GO
ALTER TABLE [aaa].[RecordAudits] CHECK CONSTRAINT [FK_RecordAudit_TrackedTable]
GO
ALTER TABLE [aaa].[Sessions]  WITH CHECK ADD  CONSTRAINT [FK_Session_Client] FOREIGN KEY([ClientId])
REFERENCES [aaa].[Clients] ([ClientId])
GO
ALTER TABLE [aaa].[Sessions] CHECK CONSTRAINT [FK_Session_Client]
GO
ALTER TABLE [aaa].[Sessions]  WITH CHECK ADD  CONSTRAINT [FK_Session_User] FOREIGN KEY([UserId])
REFERENCES [aaa].[Users] ([UserId])
GO
ALTER TABLE [aaa].[Sessions] CHECK CONSTRAINT [FK_Session_User]
GO
ALTER TABLE [aaa].[SettingValues]  WITH CHECK ADD  CONSTRAINT [FK_SettingValue_Setting] FOREIGN KEY([SettingId])
REFERENCES [aaa].[Settings] ([SettingId])
GO
ALTER TABLE [aaa].[SettingValues] CHECK CONSTRAINT [FK_SettingValue_Setting]
GO
ALTER TABLE [aaa].[SettingValues]  WITH CHECK ADD  CONSTRAINT [FK_SettingValue_User] FOREIGN KEY([UserId])
REFERENCES [aaa].[Users] ([UserId])
GO
ALTER TABLE [aaa].[SettingValues] CHECK CONSTRAINT [FK_SettingValue_User]
GO
ALTER TABLE [aaa].[SettingValues]  WITH CHECK ADD  CONSTRAINT [FK_SettingValue_UserGroup] FOREIGN KEY([UserGroupId])
REFERENCES [aaa].[UserGroups] ([UserGroupId])
GO
ALTER TABLE [aaa].[SettingValues] CHECK CONSTRAINT [FK_SettingValue_UserGroup]
GO
ALTER TABLE [aaa].[UserGroupMembers]  WITH CHECK ADD  CONSTRAINT [FK_UserGroupMember_User] FOREIGN KEY([UserId])
REFERENCES [aaa].[Users] ([UserId])
GO
ALTER TABLE [aaa].[UserGroupMembers] CHECK CONSTRAINT [FK_UserGroupMember_User]
GO
ALTER TABLE [aaa].[UserGroupMembers]  WITH CHECK ADD  CONSTRAINT [FK_UserGroupMember_UserGroup] FOREIGN KEY([UserGroupId])
REFERENCES [aaa].[UserGroups] ([UserGroupId])
GO
ALTER TABLE [aaa].[UserGroupMembers] CHECK CONSTRAINT [FK_UserGroupMember_UserGroup]
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue]  WITH CHECK ADD  CONSTRAINT [CK_ActionConstraintGroupValue_ValueLogicalOp] CHECK  (([ValueLogicalOperator] IS NULL OR ([ValueLogicalOperator]='OR' OR [ValueLogicalOperator]='AND')))
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue] CHECK CONSTRAINT [CK_ActionConstraintGroupValue_ValueLogicalOp]
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue]  WITH CHECK ADD  CONSTRAINT [CK_ActionConstraintGroupValue_ValuesLogicalOp] CHECK  (([ValuesLogicalOperator] IS NULL OR ([ValuesLogicalOperator]='OR' OR [ValuesLogicalOperator]='AND')))
GO
ALTER TABLE [aaa].[ActionConstraintGroupValue] CHECK CONSTRAINT [CK_ActionConstraintGroupValue_ValuesLogicalOp]
GO
ALTER TABLE [aaa].[ActionConstraints]  WITH CHECK ADD  CONSTRAINT [CK_ActionConstraints_Operator] CHECK  (([Operator]='matchesRegex' OR [Operator]='endsWith' OR [Operator]='startsWith' OR [Operator]='equalsIgnoreCase' OR [Operator]='contains' OR [Operator]='between' OR [Operator]='lessThanOrEqualTo' OR [Operator]='greaterThanOrEqualTo' OR [Operator]='lessThan' OR [Operator]='greaterThan' OR [Operator]='equals'))
GO
ALTER TABLE [aaa].[ActionConstraints] CHECK CONSTRAINT [CK_ActionConstraints_Operator]
GO
ALTER TABLE [aaa].[RecordAudits]  WITH CHECK ADD  CONSTRAINT [CK_RecordAudit_OperationType] CHECK  (([OperationType]='S' OR [OperationType]='D' OR [OperationType]='U' OR [OperationType]='I'))
GO
ALTER TABLE [aaa].[RecordAudits] CHECK CONSTRAINT [CK_RecordAudit_OperationType]
GO
ALTER TABLE [aaa].[SettingValues]  WITH CHECK ADD  CONSTRAINT [CK_SettingValue_ExactlyOneTarget] CHECK  (((case when [UserId] IS NULL then (0) else (1) end+case when [UserGroupId] IS NULL then (0) else (1) end)=(1)))
GO
ALTER TABLE [aaa].[SettingValues] CHECK CONSTRAINT [CK_SettingValue_ExactlyOneTarget]
GO
