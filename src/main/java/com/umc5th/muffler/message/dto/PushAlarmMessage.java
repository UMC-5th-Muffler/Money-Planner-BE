package com.umc5th.muffler.message.dto;

import lombok.Getter;

@Getter
public class PushAlarmMessage extends Message{
    private String token;

    public PushAlarmMessage(Message message, String token) {
        super(message);
        this.token = token;
    }
}
