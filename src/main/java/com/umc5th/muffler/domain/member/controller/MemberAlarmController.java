package com.umc5th.muffler.domain.member.controller;

import com.umc5th.muffler.domain.member.dto.AlarmAgreeUpdateRequest;
import com.umc5th.muffler.domain.member.dto.TokenEnrollRequest;
import com.umc5th.muffler.domain.member.service.MemberAlarmService;
import com.umc5th.muffler.global.response.Response;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/alarm")
public class MemberAlarmController {
    private final MemberAlarmService memberAlarmService;

    @PutMapping("/agree")
    public Response<Void> fetchAlarmAgree(Principal principal, @RequestBody @Valid AlarmAgreeUpdateRequest request) {
        memberAlarmService.fetchAlarmAgree(principal.getName(), request);
        return Response.success();
    }

    @PutMapping("/token")
    public Response<Void> enrollAlarmToken(Principal principal, @RequestBody @Valid TokenEnrollRequest request) {
        memberAlarmService.enrollAlarmToken(principal.getName(), request);
        return Response.success();
    }

}
