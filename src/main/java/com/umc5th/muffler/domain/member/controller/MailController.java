package com.umc5th.muffler.domain.member.controller;

import com.umc5th.muffler.domain.member.dto.InquiryRequest;
import com.umc5th.muffler.domain.member.service.MailService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/mail")
public class MailController {
    private final MailService mailService;

    @PostMapping("/inquiry")
    private Response<Void> inquiryEmail(@RequestBody @Valid InquiryRequest request, Authentication authentication){
        mailService.sendInquiryEmail(request, authentication.getName());
        return Response.success();
    }
}
