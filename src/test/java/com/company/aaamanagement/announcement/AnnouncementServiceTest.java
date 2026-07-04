package com.company.aaamanagement.announcement;

import com.company.aaamanagement.domain.Announcement;
import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock AnnouncementRepository announcementRepository;
    @Mock AnnouncementTargetRepository targetRepository;
    @Mock UserRepository userRepository;
    @Mock GroupRepository groupRepository;
    @InjectMocks AnnouncementService announcementService;

    private Announcement validForm() {
        return Announcement.builder()
                .title("Bakım duyurusu")
                .body("Sistem bakımda olacak.")
                .build();
    }

    @Test
    void saveAnnouncement_whenAckRequiredAndDismissible_throwsIllegalArgument() {
        Announcement form = validForm();
        form.setRequiresAcknowledgement(true);
        form.setDismissible(true);

        assertThatThrownBy(() -> announcementService.saveAnnouncement(form, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Onay gerektiren");

        verify(announcementRepository, never()).save(any());
    }

    @Test
    void saveAnnouncement_whenPublishUntilBeforeFrom_throwsIllegalArgument() {
        Announcement form = validForm();
        form.setDismissible(true);
        form.setPublishFromUtc(LocalDateTime.of(2026, 7, 10, 12, 0));
        form.setPublishUntilUtc(LocalDateTime.of(2026, 7, 10, 11, 0));

        assertThatThrownBy(() -> announcementService.saveAnnouncement(form, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Yayın bitişi");

        verify(announcementRepository, never()).save(any());
    }

    @Test
    void saveAnnouncement_whenNewWithoutCreator_throwsIllegalArgument() {
        assertThatThrownBy(() -> announcementService.saveAnnouncement(validForm(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Oluşturan");

        verify(announcementRepository, never()).save(any());
    }

    @Test
    void saveAnnouncement_whenNew_setsCreatorAndDefaultPublishFrom() {
        Announcement form = validForm();
        User creator = User.builder().userId(3).build();
        when(userRepository.findById(3)).thenReturn(Optional.of(creator));
        when(announcementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Announcement saved = announcementService.saveAnnouncement(form, 3);

        assertThat(saved.getCreatedByUser()).isSameAs(creator);
        assertThat(saved.getPublishFromUtc()).isNotNull();
        assertThat(saved.getModifiedAtUtc()).isNotNull();
    }

    @Test
    void saveAnnouncement_whenEdit_mergesFieldsAndKeepsCreator() {
        User creator = User.builder().userId(3).build();
        Announcement existing = Announcement.builder()
                .announcementId(9)
                .title("Eski başlık")
                .body("Eski içerik")
                .createdByUser(creator)
                .publishFromUtc(LocalDateTime.of(2026, 7, 1, 8, 0))
                .build();
        Announcement form = validForm();
        form.setAnnouncementId(9);
        form.setPublishFromUtc(LocalDateTime.of(2026, 7, 2, 8, 0));
        when(announcementRepository.findById(9)).thenReturn(Optional.of(existing));
        when(announcementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Announcement saved = announcementService.saveAnnouncement(form, null);

        assertThat(saved).isSameAs(existing);
        assertThat(saved.getTitle()).isEqualTo("Bakım duyurusu");
        assertThat(saved.getCreatedByUser()).isSameAs(creator);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void addTarget_whenBothUserAndGroup_throwsIllegalArgument() {
        assertThatThrownBy(() -> announcementService.addTarget(1, 2, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tam olarak bir hedef");

        verify(targetRepository, never()).save(any());
    }

    @Test
    void addTarget_whenNeitherUserNorGroup_throwsIllegalArgument() {
        assertThatThrownBy(() -> announcementService.addTarget(1, null, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(targetRepository, never()).save(any());
    }

    @Test
    void addTarget_whenUserAlreadyTargeted_throwsIllegalArgument() {
        when(announcementRepository.findById(1)).thenReturn(Optional.of(
                Announcement.builder().announcementId(1).build()));
        when(targetRepository.existsByAnnouncement_AnnouncementIdAndUser_UserId(1, 2)).thenReturn(true);

        assertThatThrownBy(() -> announcementService.addTarget(1, 2, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("zaten hedeflenmiş");

        verify(targetRepository, never()).save(any());
    }

    @Test
    void deleteAnnouncement_removesTargetsFirst() {
        announcementService.deleteAnnouncement(5);

        var order = inOrder(targetRepository, announcementRepository);
        order.verify(targetRepository).deleteByAnnouncement_AnnouncementId(5);
        order.verify(announcementRepository).deleteById(5);
    }
}
