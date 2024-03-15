package com.umc5th.muffler.domain.member.service;

import com.umc5th.muffler.domain.member.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleService {
    public String login(LoginRequest request) {
        return request.getIdToken();
    }
}
