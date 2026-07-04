package com.company.aaamanagement.announcement;

import com.company.aaamanagement.domain.AnnouncementTarget;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementTargetRepository extends JpaRepository<AnnouncementTarget, Integer> {

    @EntityGraph(attributePaths = {"user", "userGroup"})
    List<AnnouncementTarget> findByAnnouncement_AnnouncementIdOrderByAnnouncementTargetIdAsc(Integer announcementId);

    boolean existsByAnnouncement_AnnouncementIdAndUser_UserId(Integer announcementId, Integer userId);

    boolean existsByAnnouncement_AnnouncementIdAndUserGroup_UserGroupId(Integer announcementId, Integer userGroupId);

    void deleteByAnnouncement_AnnouncementId(Integer announcementId);
}
