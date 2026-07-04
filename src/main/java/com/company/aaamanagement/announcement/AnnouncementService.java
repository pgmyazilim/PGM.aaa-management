package com.company.aaamanagement.announcement;

import com.company.aaamanagement.domain.Announcement;
import com.company.aaamanagement.domain.AnnouncementTarget;
import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    static final List<String> BODY_FORMATS = List.of("plain", "markdown", "html");
    static final List<String> SEVERITIES = List.of("info", "success", "warning", "critical", "maintenance");

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementTargetRepository targetRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public Page<Announcement> list(String search, int page, int size) {
        return announcementRepository.findBySearch(search,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishFromUtc")));
    }

    public Announcement findById(Integer id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Duyuru bulunamadı: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("lastName", "firstName"));
    }

    public List<UserGroup> getAllGroups() {
        return groupRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Announcement saveAnnouncement(Announcement form, Integer createdByUserId) {
        if (!BODY_FORMATS.contains(form.getBodyFormat())) {
            throw new IllegalArgumentException("Geçersiz gövde formatı: " + form.getBodyFormat());
        }
        if (!SEVERITIES.contains(form.getSeverity())) {
            throw new IllegalArgumentException("Geçersiz önem seviyesi: " + form.getSeverity());
        }
        // CK_Announcements_AckVsDismiss: onay gereken duyuru kapatılabilir olamaz
        if (form.isRequiresAcknowledgement() && form.isDismissible()) {
            throw new IllegalArgumentException("Onay gerektiren bir duyuru kapatılabilir olamaz.");
        }
        if (form.getPublishFromUtc() == null) {
            form.setPublishFromUtc(LocalDateTime.now(ZoneOffset.UTC));
        }
        // CK_Announcements_PublishWindow
        if (form.getPublishUntilUtc() != null && !form.getPublishUntilUtc().isAfter(form.getPublishFromUtc())) {
            throw new IllegalArgumentException("Yayın bitişi yayın başlangıcından sonra olmalı.");
        }

        boolean isNew = form.getAnnouncementId() == null;
        Announcement announcement;
        if (isNew) {
            if (createdByUserId == null) {
                throw new IllegalArgumentException("Oluşturan kullanıcı seçilmeli.");
            }
            announcement = form;
            announcement.setCreatedByUser(userRepository.findById(createdByUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + createdByUserId)));
        } else {
            // oluşturan kullanıcı gibi form dışı alanları korumak için mevcut kayda merge et
            announcement = findById(form.getAnnouncementId());
            announcement.setTitle(form.getTitle());
            announcement.setBody(form.getBody());
            announcement.setBodyFormat(form.getBodyFormat());
            announcement.setSeverity(form.getSeverity());
            announcement.setPublishFromUtc(form.getPublishFromUtc());
            announcement.setPublishUntilUtc(form.getPublishUntilUtc());
            announcement.setActive(form.isActive());
            announcement.setGlobal(form.isGlobal());
            announcement.setPinned(form.isPinned());
            announcement.setRequiresAcknowledgement(form.isRequiresAcknowledgement());
            announcement.setDismissible(form.isDismissible());
            announcement.setExtraInfo(form.getExtraInfo());
        }
        announcement.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Integer id) {
        targetRepository.deleteByAnnouncement_AnnouncementId(id);
        announcementRepository.deleteById(id);
    }

    public List<AnnouncementTarget> getTargets(Integer announcementId) {
        return targetRepository.findByAnnouncement_AnnouncementIdOrderByAnnouncementTargetIdAsc(announcementId);
    }

    // CK_AnnouncementTargets_ExactlyOneTarget: UserId VEYA UserGroupId
    @Transactional
    public AnnouncementTarget addTarget(Integer announcementId, Integer userId, Integer userGroupId) {
        if ((userId == null) == (userGroupId == null)) {
            throw new IllegalArgumentException(
                    "Duyuru hedefi tam olarak bir hedef içermeli: kullanıcı VEYA grup.");
        }
        Announcement announcement = findById(announcementId);
        AnnouncementTarget.AnnouncementTargetBuilder builder = AnnouncementTarget.builder()
                .announcement(announcement);
        if (userId != null) {
            if (targetRepository.existsByAnnouncement_AnnouncementIdAndUser_UserId(announcementId, userId)) {
                throw new IllegalArgumentException("Bu kullanıcı duyuruda zaten hedeflenmiş.");
            }
            builder.user(userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + userId)));
        } else {
            if (targetRepository.existsByAnnouncement_AnnouncementIdAndUserGroup_UserGroupId(announcementId, userGroupId)) {
                throw new IllegalArgumentException("Bu grup duyuruda zaten hedeflenmiş.");
            }
            builder.userGroup(groupRepository.findById(userGroupId)
                    .orElseThrow(() -> new EntityNotFoundException("Grup bulunamadı: " + userGroupId)));
        }
        return targetRepository.save(builder.build());
    }

    @Transactional
    public void deleteTarget(Integer id) {
        targetRepository.deleteById(id);
    }
}
