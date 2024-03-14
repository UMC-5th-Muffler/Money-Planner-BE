package com.umc5th.muffler.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InquiryRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String content;
}
