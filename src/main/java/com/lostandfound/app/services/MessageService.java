package com.lostandfound.app.services;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.Item;
import com.lostandfound.app.entities.Message;
import com.lostandfound.app.repositories.MessageRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager entityManager;

    // ✅ FIXED: Changed parameter name from "messageText" to "content" to match controller
    public Message sendMessage(Integer senderId, Integer recipientId, Integer itemId, String content) {

        if (itemId == null) {
            throw new IllegalArgumentException("itemId is required.");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content is required.");
        }

        AppUser sender = entityManager.getReference(AppUser.class, senderId);
        AppUser recipient = entityManager.getReference(AppUser.class, recipientId);
        Item item = entityManager.getReference(Item.class, itemId);

        Message m = new Message();
        m.setSender(sender);
        m.setRecipient(recipient);
        m.setItem(item);
        m.setMessageText(content.trim());  // ✅ Still using setMessageText() as per entity
        m.setCreatedAt(LocalDateTime.now()); // createdAt is NOT NULL in your entity

        return messageRepository.save(m);
    }

    public List<Message> getThread(Integer userA, Integer userB, Integer itemId) {
        return messageRepository.getThread(userA, userB, itemId);
    }

    public List<Message> openThread(Integer me, Integer other, Integer itemId) {
        messageRepository.markThreadAsRead(me, other, itemId);
        return messageRepository.getThread(me, other, itemId);
    }
}
