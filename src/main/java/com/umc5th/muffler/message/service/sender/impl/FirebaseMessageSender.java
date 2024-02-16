package com.umc5th.muffler.message.service.sender.impl;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.umc5th.muffler.message.dto.Alarmable;
import com.umc5th.muffler.message.dto.MessageDTO;
import com.umc5th.muffler.message.service.sender.Sender;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirebaseMessageSender implements Sender {
    private static final int MAX_BATCH_SIZE = 500;
    @Override
    public <T extends Alarmable> int send(List<T> data, Function<T, MessageDTO> formatter) {
        List<Message> pushAlarms = data.stream().map(datum -> {
            MessageDTO message = formatter.apply(datum);
            return Message.builder()
                    .setNotification(
                            Notification.builder()
                                    .setTitle(message.getTitle())
                                    .setBody(message.getBody())
                                    .setImage(message.getImageUrl())
                                    .build()
                    )
                    .setToken(datum.getToken())
                    .build();
        }).collect(Collectors.toList());
        return batchSend(pushAlarms);
    }

    private int batchSend(List<Message> messages) {
        List<SendResponse> failResponses = new ArrayList<>();
        List<Message> batchMessages = new ArrayList<>();

        messages.forEach(message -> {
            batchMessages.add(message);
            if (batchMessages.size() == MAX_BATCH_SIZE) {
                trySend(failResponses, batchMessages);
            }
        });
        if (!batchMessages.isEmpty()) {
            trySend(failResponses, batchMessages);
        }
        failResponses.forEach(res -> log.error("{} : {}", res.getMessageId(), res.getException().getMessage()));
        return messages.size() - failResponses.size();
    }

    private static void trySend(List<SendResponse> failResponses, List<Message> batchMessages) {
        try {
            BatchResponse batchResponse = FirebaseMessaging.getInstance().sendEach(batchMessages);
            List<SendResponse> responses = batchResponse.getResponses();
            responses.forEach(sendResponse -> {
                if (!sendResponse.isSuccessful()) {
                    failResponses.add(sendResponse);
                }
            });
            batchMessages.clear();
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
        }
    }
}
