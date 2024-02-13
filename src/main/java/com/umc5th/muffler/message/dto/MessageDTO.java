package com.umc5th.muffler.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageDTO {
    protected String title;
    protected String body;

    public MessageDTO(MessageDTO message) {
        this.title = message.title;
        this.body = message.body;
    }
}
