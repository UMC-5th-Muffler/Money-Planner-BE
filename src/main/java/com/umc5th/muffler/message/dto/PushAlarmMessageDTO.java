package com.umc5th.muffler.message.dto;

import lombok.Getter;

@Getter
public class PushAlarmMessageDTO extends MessageDTO {
    private String token;

    public PushAlarmMessageDTO(MessageDTO message, String token) {
        super(message.title, message.body, message.imageUrl);
        this.token = token;
    }
}
