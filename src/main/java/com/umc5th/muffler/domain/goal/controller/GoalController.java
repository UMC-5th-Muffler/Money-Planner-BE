package com.umc5th.muffler.domain.goal.controller;

import com.umc5th.muffler.domain.expense.dto.WeekRequestParam;
import com.umc5th.muffler.domain.goal.dto.*;
import com.umc5th.muffler.domain.goal.service.GoalCreateService;
import com.umc5th.muffler.domain.goal.service.GoalService;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goal")
public class GoalController {

    private final GoalService goalService;
    private final GoalCreateService goalCreateService;

    @PostMapping
    public Response<Void> create(@RequestBody @Valid GoalCreateRequest request, Authentication authentication) {
        goalCreateService.create(request, authentication.getName());
        return Response.success();
    }

    @GetMapping("/previous")
    public Response<GoalPreviousResponse> getPrevious(Authentication authentication) {
        List<Goal> goals = goalService.getGoals(authentication.getName());
        return Response.success(GoalConverter.getGoalPreviousResponse(goals));
    }

    @DeleteMapping("/{goalId}")
    public Response<Void> delete(@PathVariable Long goalId, Authentication authentication) {
        goalService.delete(goalId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/now")
    public Response<GoalInfo> getNowGoal(Authentication authentication) {
        GoalInfo response = goalService.getGoalNow(authentication.getName());
        return Response.success(response);
    }

    @GetMapping("/not-now")
    public Response<GoalPreviewResponse> getGoalPreview(Authentication authentication,
            @RequestParam (name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        GoalPreviewResponse response = goalService.getGoalPreview(authentication.getName(), pageable, endDate);
        return Response.success(response);
    }

    @GetMapping("/list")
    public Response<GoalListResponse> getGoalList(Authentication authentication) {
        GoalListResponse response = goalService.getGoalList(authentication.getName());
        return Response.success(response);
    }
}
