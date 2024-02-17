package com.umc5th.muffler.message.service.sender;

import com.umc5th.muffler.message.dto.Alarmable;
import com.umc5th.muffler.message.dto.MessageDTO;
import java.util.List;
import java.util.function.Function;

public interface Sender {
    <T extends Alarmable> int send(List<T> data, Function<T, MessageDTO> formatter);
}
