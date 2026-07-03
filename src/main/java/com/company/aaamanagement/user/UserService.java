package com.company.aaamanagement.user;

import com.company.aaamanagement.domain.User;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.domain.UserGroupMember;
import com.company.aaamanagement.group.GroupRepository;
import com.company.aaamanagement.group.UserGroupMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserGroupMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> list(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName", "firstName"));
        return userRepository.findBySearch(search, pageable);
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + id));
    }

    @Transactional
    public User save(User form, String rawPassword) {
        boolean isNew = form.getUserId() == null;
        if (isNew) {
            if (userRepository.existsByUsername(form.getUsername())) {
                throw new IllegalArgumentException("Bu kullanıcı adı zaten kullanılıyor: " + form.getUsername());
            }
        } else {
            if (userRepository.existsByUsernameAndUserIdNot(form.getUsername(), form.getUserId())) {
                throw new IllegalArgumentException("Bu kullanıcı adı zaten kullanılıyor: " + form.getUsername());
            }
        }
        validateEmailUnique(form);

        User user = isNew ? form : mergeIntoExisting(form);

        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            user.setLastPasswordChangeUtc(LocalDateTime.now(ZoneOffset.UTC));
        } else if (isNew) {
            throw new IllegalArgumentException("Yeni kullanıcı için parola zorunludur.");
        }

        user.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return userRepository.save(user);
    }

    // Form yalnızca düzenlenebilir alanları gönderir; hash, OTP secret'ları ve
    // kilit sayaçları gibi form dışı alanlar mevcut kayıttan korunur.
    private User mergeIntoExisting(User form) {
        User user = findById(form.getUserId());
        user.setFirstName(form.getFirstName());
        user.setMiddleName(form.getMiddleName());
        user.setLastName(form.getLastName());
        user.setUsername(form.getUsername());
        user.setEmailUser(form.getEmailUser());
        user.setEmailDomain(form.getEmailDomain());
        user.setBadgeNo(form.getBadgeNo());
        user.setIdNo(form.getIdNo());
        user.setWelcomeMessage(form.getWelcomeMessage());
        user.setActive(form.isActive());
        user.setSuperUser(form.isSuperUser());
        user.setAlwaysUseOtp(form.getAlwaysUseOtp());
        return user;
    }

    private void validateEmailUnique(User form) {
        if (form.getEmailUser() == null || form.getEmailUser().isBlank()
                || form.getEmailDomain() == null || form.getEmailDomain().isBlank()) {
            return;
        }
        boolean exists = form.getUserId() == null
                ? userRepository.existsByEmailUserIgnoreCaseAndEmailDomainIgnoreCase(
                        form.getEmailUser(), form.getEmailDomain())
                : userRepository.existsByEmailUserIgnoreCaseAndEmailDomainIgnoreCaseAndUserIdNot(
                        form.getEmailUser(), form.getEmailDomain(), form.getUserId());
        if (exists) {
            throw new IllegalArgumentException("Bu e-posta adresi başka bir kullanıcıya kayıtlı: "
                    + form.getEmailUser() + "@" + form.getEmailDomain());
        }
    }

    @Transactional
    public void unlock(Integer id) {
        User user = findById(id);
        user.setFailedLoginCount(0);
        user.setLockedUntilUtc(null);
        user.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
    }

    @Transactional
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    public List<UserGroupMember> getMemberships(Integer userId) {
        return memberRepository.findByUser_UserId(userId);
    }

    public List<UserGroup> getAllGroups() {
        return groupRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public void assignGroupToUser(Integer userId, Integer groupId) {
        if (memberRepository.existsByUser_UserIdAndUserGroup_UserGroupId(userId, groupId)) {
            throw new IllegalStateException("Kullanıcı zaten bu grubun üyesi.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Kullanıcı bulunamadı: " + userId));
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grup bulunamadı: " + groupId));
        memberRepository.save(UserGroupMember.builder().user(user).userGroup(group).build());
    }

    @Transactional
    public void removeGroupFromUser(Integer userId, Integer groupId) {
        memberRepository.findByUser_UserIdAndUserGroup_UserGroupId(userId, groupId)
                .ifPresent(memberRepository::delete);
    }

    @Transactional
    public void syncGroups(Integer userId, List<Integer> groupIds) {
        List<UserGroupMember> existing = memberRepository.findByUser_UserId(userId);
        existing.stream()
                .filter(m -> !groupIds.contains(m.getUserGroup().getUserGroupId()))
                .forEach(memberRepository::delete);
        groupIds.stream()
                .filter(gid -> existing.stream()
                        .noneMatch(m -> m.getUserGroup().getUserGroupId().equals(gid)))
                .forEach(gid -> assignGroupToUser(userId, gid));
    }
}
