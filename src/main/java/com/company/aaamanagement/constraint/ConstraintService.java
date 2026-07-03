package com.company.aaamanagement.constraint;

import com.company.aaamanagement.action.ActionRepository;
import com.company.aaamanagement.domain.Action;
import com.company.aaamanagement.domain.ActionConstraint;
import com.company.aaamanagement.domain.ActionConstraintGroupValue;
import com.company.aaamanagement.domain.ConstraintOperator;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.group.GroupRepository;
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
public class ConstraintService {

    private final ActionConstraintRepository constraintRepository;
    private final ActionConstraintGroupValueRepository groupValueRepository;
    private final ActionRepository actionRepository;
    private final GroupRepository groupRepository;

    public Page<ActionConstraint> list(Integer actionId, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return constraintRepository.findByActionAndSearch(actionId, search, pageable);
    }

    public ActionConstraint findById(Integer id) {
        return constraintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kısıt bulunamadı: " + id));
    }

    public List<Action> getAllActions() {
        return actionRepository.findAll(Sort.by("name"));
    }

    public ConstraintOperator[] getOperators() {
        return ConstraintOperator.values();
    }

    @Transactional
    public ActionConstraint save(ActionConstraint constraint) {
        constraint.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return constraintRepository.save(constraint);
    }

    @Transactional
    public void delete(Integer id) {
        constraintRepository.deleteById(id);
    }

    public List<ActionConstraintGroupValue> getGroupValues(Integer constraintId) {
        return groupValueRepository.findByActionConstraint_ActionConstraintId(constraintId);
    }

    public List<UserGroup> getAllGroups() {
        return groupRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public ActionConstraintGroupValue saveGroupValue(ActionConstraintGroupValue value) {
        value.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return groupValueRepository.save(value);
    }

    @Transactional
    public ActionConstraintGroupValue upsertGroupValue(Integer constraintId, Integer groupId,
                                                        String valueList, String delimiter,
                                                        String valuesLogicalOp, String valueLogicalOp) {
        ActionConstraint constraint = constraintRepository.findById(constraintId)
                .orElseThrow(() -> new EntityNotFoundException("Kısıt bulunamadı: " + constraintId));
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grup bulunamadı: " + groupId));

        ActionConstraintGroupValue val = groupValueRepository
                .findByActionConstraint_ActionConstraintIdAndUserGroup_UserGroupId(constraintId, groupId)
                .orElse(ActionConstraintGroupValue.builder()
                        .actionConstraint(constraint).userGroup(group).build());

        val.setValueList(valueList);
        val.setValueDelimiter(delimiter);
        val.setValuesLogicalOperator(valuesLogicalOp);
        val.setValueLogicalOperator(valueLogicalOp);
        val.setModifiedAtUtc(LocalDateTime.now(ZoneOffset.UTC));
        return groupValueRepository.save(val);
    }

    @Transactional
    public void deleteGroupValue(Integer id) {
        groupValueRepository.deleteById(id);
    }
}
