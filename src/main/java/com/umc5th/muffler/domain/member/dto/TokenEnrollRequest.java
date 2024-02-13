package com.umc5th.muffler.domain.member.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenEnrollRequest {
    @NotEmpty
    private String token;
}
