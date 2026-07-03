package com.company.aaamanagement.action;

import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.Module;
import com.company.aaamanagement.infrastructure.ModuleRepository;
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
public class ActionService {

    private final ActionRepository actionRepository;
    private final ModuleRepository moduleRepository;

    public Page<Action> list(Integer moduleId, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return actionRepository.findByModuleAndSearch(moduleId, search, pageable);
    }

    public Action findById(Integer id) {
        return actionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("İşlem bulunamadı: " + id));
    }

    public List<Module> getAllModules() {
        return moduleRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Action save(Action action) {
        if (action.getActionId() == null) {
            if (actionRepository.existsByActionKey(action.getActionKey())) {
                throw new IllegalArgumentException("Bu actionKey zaten kullanılıyor: " + action.getActionKey());
            }
        } else {
            if (actionRepository.existsByActionKeyAndActionIdNot(action.getActionKey(), action.getActionId())) {
                throw new IllegalArgumentException("Bu actionKey zaten kullanılıyor: " + action.getActionKey());
            }
        }
        action.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return actionRepository.save(action);
    }

    @Transactional
    public void delete(Integer id) {
        actionRepository.deleteById(id);
    }
}
