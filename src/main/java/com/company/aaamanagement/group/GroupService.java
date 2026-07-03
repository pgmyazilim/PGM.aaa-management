package com.company.aaamanagement.group;

import com.company.aaamanagement.domain.UserGroup;
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

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserGroupMemberRepository memberRepository;

    public Page<UserGroup> list(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return groupRepository.findBySearch(search, pageable);
    }

    public UserGroup findById(Integer id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grup bulunamadı: " + id));
    }

    @Transactional
    public UserGroup save(UserGroup group) {
        if (group.getUserGroupId() == null) {
            if (groupRepository.existsByName(group.getName())) {
                throw new IllegalArgumentException("Bu grup adı zaten mevcut: " + group.getName());
            }
        } else {
            if (groupRepository.existsByNameAndUserGroupIdNot(group.getName(), group.getUserGroupId())) {
                throw new IllegalArgumentException("Bu grup adı zaten mevcut: " + group.getName());
            }
        }
        group.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return groupRepository.save(group);
    }

    @Transactional
    public void delete(Integer id) {
        groupRepository.deleteById(id);
    }

    public long getMemberCount(Integer groupId) {
        return memberRepository.countByGroupId(groupId);
    }
}
