package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.TrackedTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackedTableServiceTest {

    @Mock TrackedTableRepository trackedTableRepository;
    @Mock RecordAuditRepository recordAuditRepository;
    @InjectMocks TrackedTableService service;

    @Test
    void save_whenNameBlank_throwsIllegalArgument() {
        TrackedTable form = TrackedTable.builder().name("  ").build();

        assertThatThrownBy(() -> service.save(form, List.of("I"), List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ad");

        verify(trackedTableRepository, never()).save(any());
    }

    @Test
    void save_whenNewAndNameDuplicate_throwsIllegalArgument() {
        TrackedTable form = TrackedTable.builder().name("aaa.Users").build();
        when(trackedTableRepository.existsByNameIgnoreCase("aaa.Users")).thenReturn(true);

        assertThatThrownBy(() -> service.save(form, List.of(), List.of()))
                .isInstanceOf(IllegalArgumentException.class);

        verify(trackedTableRepository, never()).save(any());
    }

    @Test
    void save_whenEditingSameNameOnOwnRow_isAllowed() {
        TrackedTable form = TrackedTable.builder().trackedTableId(7).name("aaa.Users").build();
        when(trackedTableRepository.existsByNameIgnoreCaseAndTrackedTableIdNot("aaa.Users", 7))
                .thenReturn(false);
        when(trackedTableRepository.findById(7)).thenReturn(Optional.of(
                TrackedTable.builder().trackedTableId(7).name("aaa.Users").build()));
        when(trackedTableRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrackedTable saved = service.save(form, List.of(), List.of());

        assertThat(saved.getTrackedTableId()).isEqualTo(7);
        verify(trackedTableRepository).save(any());
    }

    @Test
    void save_normalizesTrackingTypesToCanonicalOrder() {
        TrackedTable form = TrackedTable.builder().name("aaa.Sessions").build();
        when(trackedTableRepository.existsByNameIgnoreCase("aaa.Sessions")).thenReturn(false);
        when(trackedTableRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrackedTable saved = service.save(form, List.of("D", "I", "U"), List.of());

        assertThat(saved.getActorTrackingTypes()).isEqualTo("IUD");
        assertThat(saved.getRecordTrackingTypes()).isNull();
        assertThat(saved.getModifiedAtUtc()).isNotNull();
    }

    @Test
    void delete_whenReferencedByRecordAudits_throwsIllegalState() {
        when(recordAuditRepository.existsByTrackedTable_TrackedTableId(3)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(3))
                .isInstanceOf(IllegalStateException.class);

        verify(trackedTableRepository, never()).deleteById(any());
    }

    @Test
    void delete_whenNotReferenced_deletes() {
        when(recordAuditRepository.existsByTrackedTable_TrackedTableId(3)).thenReturn(false);

        service.delete(3);

        verify(trackedTableRepository).deleteById(3);
    }
}
