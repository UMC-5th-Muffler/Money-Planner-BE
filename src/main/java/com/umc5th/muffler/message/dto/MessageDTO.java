package com.umc5th.muffler.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageDTO {
    protected String title;
    protected String body;
    protected String imageUrl;
}
