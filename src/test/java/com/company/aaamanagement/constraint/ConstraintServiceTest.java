package com.company.aaamanagement.constraint;

import com.company.aaamanagement.action.ActionRepository;
import com.company.aaamanagement.domain.ActionConstraint;
import com.company.aaamanagement.domain.ActionConstraintGroupValue;
import com.company.aaamanagement.domain.UserGroup;
import com.company.aaamanagement.group.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConstraintServiceTest {

    @Mock ActionConstraintRepository constraintRepository;
    @Mock ActionConstraintGroupValueRepository groupValueRepository;
    @Mock ActionRepository actionRepository;
    @Mock GroupRepository groupRepository;
    @InjectMocks ConstraintService constraintService;

    @Test
    void saveGroupValue_withInvalidValueLogicalOperator_throwsIllegalArgument() {
        ActionConstraintGroupValue value = ActionConstraintGroupValue.builder()
                .valueLogicalOperator("XOR").build();

        assertThatThrownBy(() -> constraintService.saveGroupValue(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AND veya OR");

        verify(groupValueRepository, never()).save(any());
    }

    @Test
    void upsertGroupValue_withValidOperator_saves() {
        when(constraintRepository.findById(1))
                .thenReturn(Optional.of(ActionConstraint.builder().actionConstraintId(1).build()));
        when(groupRepository.findById(2))
                .thenReturn(Optional.of(UserGroup.builder().userGroupId(2).build()));
        when(groupValueRepository.findByActionConstraint_ActionConstraintIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.empty());
        when(groupValueRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ActionConstraintGroupValue result =
                constraintService.upsertGroupValue(1, 2, "1,2,3", ",", "OR", "AND");

        assertThat(result.getValueLogicalOperator()).isEqualTo("AND");
    }

    @Test
    void upsertGroupValue_withBlankOperator_normalizesToNull() {
        when(constraintRepository.findById(1))
                .thenReturn(Optional.of(ActionConstraint.builder().actionConstraintId(1).build()));
        when(groupRepository.findById(2))
                .thenReturn(Optional.of(UserGroup.builder().userGroupId(2).build()));
        when(groupValueRepository.findByActionConstraint_ActionConstraintIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.empty());
        when(groupValueRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ActionConstraintGroupValue result =
                constraintService.upsertGroupValue(1, 2, "1", ",", null, "");

        assertThat(result.getValueLogicalOperator()).isNull();
    }

    @Test
    void upsertGroupValue_withInvalidOperator_throwsIllegalArgument() {
        when(constraintRepository.findById(1))
                .thenReturn(Optional.of(ActionConstraint.builder().actionConstraintId(1).build()));
        when(groupRepository.findById(2))
                .thenReturn(Optional.of(UserGroup.builder().userGroupId(2).build()));
        when(groupValueRepository.findByActionConstraint_ActionConstraintIdAndUserGroup_UserGroupId(1, 2))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> constraintService.upsertGroupValue(1, 2, "1", ",", null, "NOT"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(groupValueRepository, never()).save(any());
    }
}
