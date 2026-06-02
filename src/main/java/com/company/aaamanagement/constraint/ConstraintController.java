package com.company.aaamanagement.constraint;

import com.company.aaamanagement.domain.ActionConstraint;
import com.company.aaamanagement.domain.ActionConstraintGroupValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/constraints")
@RequiredArgsConstructor
public class ConstraintController {

    private final ConstraintService constraintService;

    @GetMapping
    public String list(@RequestParam(required = false) Integer actionId,
                       @RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<ActionConstraint> constraints = constraintService.list(actionId, search, page, size);
        model.addAttribute("constraints", constraints);
        model.addAttribute("actions", constraintService.getAllActions());
        model.addAttribute("selectedActionId", actionId);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "constraints");
        return "constraint/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Integer actionId, Model model) {
        model.addAttribute("constraint", new ActionConstraint());
        model.addAttribute("actions", constraintService.getAllActions());
        model.addAttribute("operators", constraintService.getOperators());
        model.addAttribute("selectedActionId", actionId);
        model.addAttribute("activePage", "constraints");
        return "constraint/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        ActionConstraint constraint = constraintService.findById(id);
        model.addAttribute("constraint", constraint);
        model.addAttribute("actions", constraintService.getAllActions());
        model.addAttribute("operators", constraintService.getOperators());
        model.addAttribute("groupValues", constraintService.getGroupValues(id));
        model.addAttribute("allGroups", constraintService.getAllGroups());
        model.addAttribute("activePage", "constraints");
        return "constraint/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ActionConstraint constraint) {
        constraintService.save(constraint);
        return "redirect:/constraints";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        constraintService.delete(id);
        return "redirect:/constraints";
    }

    @PostMapping("/{id}/group-values/upsert")
    public String upsertGroupValue(@PathVariable Integer id,
                                    @RequestParam Integer groupId,
                                    @RequestParam String valueList,
                                    @RequestParam(required = false) String valueDelimiter,
                                    @RequestParam(required = false) String valuesLogicalOperator,
                                    @RequestParam(required = false) String valueLogicalOperator) {
        constraintService.upsertGroupValue(id, groupId, valueList, valueDelimiter,
                valuesLogicalOperator, valueLogicalOperator);
        return "redirect:/constraints/" + id + "/edit";
    }

    @PostMapping("/group-values/{valueId}/delete")
    public String deleteGroupValue(@PathVariable Integer valueId,
                                    @RequestParam Integer constraintId) {
        constraintService.deleteGroupValue(valueId);
        return "redirect:/constraints/" + constraintId + "/edit";
    }
}
