# AAAManagement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Spring Boot MVC admin application for managing the AAA database — users, groups, actions, permissions, constraints, audit logs, settings, and infrastructure.

**Architecture:** Feature-slice packages (`user/`, `group/`, `action/`, etc.) sharing a common `domain/` package for JPA entities. No authentication. HTMX for partial updates (permission toggle, group assignment sliding panel). Tailwind CSS CDN.

**Tech Stack:** Java 25, Spring Boot 4.0.1, Gradle Groovy DSL, SQL Server 2019, Hibernate 6 / Spring Data JPA (`SQLServerDialect`), Thymeleaf 3, Tailwind CSS CDN, HTMX 2.x CDN, Lombok

---

### Task 1: Project scaffolding & configuration
- [ ] Create `settings.gradle`
- [ ] Create `.gitignore`
- [ ] Create `build.gradle`
- [ ] Create `src/main/resources/application.yml`
- [ ] Create `src/main/java/com/company/aaamanagement/AAAManagementApplication.java`
- [ ] `git commit -m "chore: initial project setup"`

### Task 2: Domain entities
- [ ] Create all 21 `@Entity` classes + 2 enums in `domain/`
- [ ] `git commit -m "feat: add domain entities"`

### Task 3: Repositories
- [ ] Create all 19 repository interfaces
- [ ] `git commit -m "feat: add repositories"`

### Task 4: Service layer + unit tests
- [ ] UserService, GroupService, ActionService, PermissionService, ConstraintService, AuditService, SettingService, InfrastructureService
- [ ] Unit tests for UserService (group assign), PermissionService (upsert), SettingService (exclusive target)
- [ ] `git commit -m "feat: add service layer"`

### Task 5: Config & Controllers
- [ ] WebMvcConfig, GlobalExceptionHandler
- [ ] All 8 controllers with pagination, filtering, HTMX support
- [ ] `git commit -m "feat: add MVC controllers"`

### Task 6: Thymeleaf templates
- [ ] layout/sidebar.html, index.html, error.html
- [ ] user/, group/, permission/, action/, constraint/, audit/, setting/, infrastructure/ templates
- [ ] `git commit -m "feat: add thymeleaf templates"`
