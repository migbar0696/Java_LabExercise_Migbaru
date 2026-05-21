package com.chatapp.service;

import com.chatapp.model.Message;

public class MessageService {
    public String formatOutgoingMessage(Message message) {
        return message.format();
    }
}
