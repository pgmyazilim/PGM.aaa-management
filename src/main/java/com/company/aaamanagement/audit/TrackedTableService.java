package com.company.aaamanagement.audit;

import com.company.aaamanagement.domain.TrackedTable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackedTableService {

    // OperationType kanonik sırası: Insert, Update, Delete, Select
    private static final List<String> CANONICAL_TYPES = List.of("I", "U", "D", "S");

    private final TrackedTableRepository trackedTableRepository;
    private final RecordAuditRepository recordAuditRepository;

    public Page<TrackedTable> listTrackedTables(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return trackedTableRepository.findBySearch(search, pageable);
    }

    public TrackedTable findById(Integer id) {
        return trackedTableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("İzlenen tablo bulunamadı: " + id));
    }

    @Transactional
    public TrackedTable save(TrackedTable form, List<String> actorTypes, List<String> recordTypes) {
        if (form.getName() == null || form.getName().isBlank()) {
            throw new IllegalArgumentException("Tablo adı boş olamaz.");
        }
        String name = form.getName().trim();
        boolean isNew = form.getTrackedTableId() == null;
        boolean duplicate = isNew
                ? trackedTableRepository.existsByNameIgnoreCase(name)
                : trackedTableRepository.existsByNameIgnoreCaseAndTrackedTableIdNot(name, form.getTrackedTableId());
        if (duplicate) {
            throw new IllegalArgumentException("Bu tablo adı zaten kayıtlı: " + name);
        }

        TrackedTable entity = isNew ? form : mergeIntoExisting(form);
        entity.setName(name);
        entity.setDescription(blankToNull(form.getDescription()));
        entity.setActorTrackingTypes(normalizeTrackingTypes(actorTypes));
        entity.setRecordTrackingTypes(normalizeTrackingTypes(recordTypes));
        entity.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return trackedTableRepository.save(entity);
    }

    @Transactional
    public void delete(Integer id) {
        if (recordAuditRepository.existsByTrackedTable_TrackedTableId(id)) {
            throw new IllegalStateException(
                    "Bu tabloya bağlı denetim kayıtları olduğu için silinemez.");
        }
        trackedTableRepository.deleteById(id);
    }

    // form yalnızca düzenlenebilir alanları taşır; CreatedAtUtc/RowVersion gibi
    // form dışı alanlar mevcut kayıttan korunur.
    private TrackedTable mergeIntoExisting(TrackedTable form) {
        return findById(form.getTrackedTableId());
    }

    private String normalizeTrackingTypes(List<String> selected) {
        if (selected == null || selected.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String type : CANONICAL_TYPES) {
            if (selected.contains(type)) {
                sb.append(type);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
