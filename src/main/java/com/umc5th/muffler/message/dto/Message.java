package com.umc5th.muffler.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {
    protected String title;
    protected String body;

    public Message(Message message) {
        this.title = message.title;
        this.body = message.body;
    }
}
