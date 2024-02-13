package com.umc5th.muffler.message.service.pushAlarm;


import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.umc5th.muffler.message.dto.MessageDTO;

public class FirebaseMessageFormatter {
    public static Message toDailyPlanRemind(MessageDTO dto) {
        return Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(dto.getTitle())
                                .setBody(dto.getBody())
                                .setImage(dto.getImageUrl())
                                .build()
                )
                .build();
    }
}
