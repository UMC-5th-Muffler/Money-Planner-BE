package com.umc5th.muffler.domain.goal.controller;

import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.dto.GoalPreviousResponse;
import com.umc5th.muffler.domain.goal.dto.GoalTerm;
import com.umc5th.muffler.domain.goal.service.GoalService;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.global.response.Response;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public Response<Void> create(@RequestBody GoalCreateRequest request, @RequestParam Long memberId) {
        goalService.create(request, memberId);
        return Response.success();
    }

    @GetMapping("/previous")
    public Response<GoalPreviousResponse> getPrevious(@RequestParam Long memberId) {
        List<Goal> goals = goalService.getGoals(memberId);
        GoalPreviousResponse response = new GoalPreviousResponse(
                goals.stream()
                        .sorted(Comparator.comparing(Goal::getStartDate).reversed())
                        .map(goal -> new GoalTerm(goal.getStartDate(), goal.getEndDate()))
                        .collect(Collectors.toList())
        );
        return Response.success(response);
    }

    @DeleteMapping("/{goalId}")
    public Response<Void> delete(@PathVariable Long goalId, @RequestParam Long memberId) {
        goalService.delete(goalId, memberId);
        return Response.success();
    }
}
