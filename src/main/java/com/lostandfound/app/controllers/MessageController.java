package com.lostandfound.app.controllers;

import com.lostandfound.app.entities.AppUser;
import com.lostandfound.app.entities.Item;
import com.lostandfound.app.entities.Message;
import com.lostandfound.app.repositories.ItemRepository;
import com.lostandfound.app.repositories.MessageRepository;
import com.lostandfound.app.repositories.UserRepository;
import com.lostandfound.app.services.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;


    public static class MessageDTO {
        public Integer messageId;
        public String senderName;
        public String senderEmail;
        public String messageText;
        public String createdAt;

        public MessageDTO(Message msg) {
            this.messageId = msg.getMessageId();
            this.senderName = msg.getSender() != null ? msg.getSender().getFullName() : "Unknown";
            this.senderEmail = msg.getSender() != null ? msg.getSender().getEmail() : "Unknown";
            this.messageText = msg.getMessageText();
            this.createdAt = msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : "";
        }
    }

    //Show actual messages grouped by conversation
    @GetMapping("/messages")
    public String viewMessages(HttpSession session, ModelMap model) {
        AppUser user = (AppUser) session.getAttribute("loggedinuser");
        if (user == null) return "redirect:/login";

        // Get all messages where user is sender or recipient
        List<Message> allMessages = messageRepository.findBySenderUserIdOrRecipientUserId(
                user.getUserId(), user.getUserId()
        );

        // Group messages by the other person (conversation partner)
        Map<Integer, List<Message>> conversations = new HashMap<>();

        for (Message msg : allMessages) {
            Integer otherUserId;
            if (msg.getSender() != null && msg.getSender().getUserId().equals(user.getUserId())) {
                // User is sender, group by recipient
                otherUserId = msg.getRecipient() != null ? msg.getRecipient().getUserId() : null;
            } else {
                // User is recipient, group by sender
                otherUserId = msg.getSender() != null ? msg.getSender().getUserId() : null;
            }

            if (otherUserId != null) {
                conversations.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(msg);
            }
        }

        // Sort each conversation by date
        conversations.values().forEach(msgs ->
                msgs.sort(Comparator.comparing(Message::getCreatedAt).reversed())
        );

        model.addAttribute("conversations", conversations);
        model.addAttribute("currentUserId", user.getUserId());
        return "messages";
    }

    //  View specific conversation thread using getThread() method
    @GetMapping("/messages/thread/{userId}/{itemId}")
    public String viewThread(@PathVariable Integer userId,
                             @PathVariable Integer itemId,
                             HttpSession session,
                             ModelMap model) {
        AppUser currentUser = (AppUser) session.getAttribute("loggedinuser");
        if (currentUser == null) return "redirect:/login";

        // Get the other user
        Optional<AppUser> otherUserOpt = userRepository.findById(userId);
        if (!otherUserOpt.isPresent()) {
            return "redirect:/messages";
        }

        // Get the item
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            return "redirect:/messages";
        }

        // Use the existing getThread() method from MessageRepository
        List<Message> messages = messageRepository.getThread(
                currentUser.getUserId(),
                userId,
                itemId
        );

        model.addAttribute("messages", messages);
        model.addAttribute("otherUser", otherUserOpt.get());
        model.addAttribute("item", itemOpt.get());
        model.addAttribute("currentUserId", currentUser.getUserId());

        return "message-thread";
    }

    @PostMapping("/messages/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam("itemId") Integer itemId,
            @RequestParam("content") String content,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Object obj = session.getAttribute("loggedinuser");
            if (!(obj instanceof AppUser)) {
                response.put("success", false);
                response.put("error", "❌ Not logged in");
                return ResponseEntity.ok(response);
            }

            AppUser sender = (AppUser) obj;
            Item item = itemRepository.findById(itemId).orElse(null);

            if (item == null) {
                response.put("success", false);
                response.put("error", "❌ Item not found");
                return ResponseEntity.ok(response);
            }

            AppUser recipient = item.getUser();
            if (recipient == null) {
                response.put("success", false);
                response.put("error", "❌ Item owner not found");
                return ResponseEntity.ok(response);
            }

            if (sender.getUserId().equals(recipient.getUserId())) {
                response.put("success", false);
                response.put("error", "❌ You cannot message yourself");
                return ResponseEntity.ok(response);
            }


            Message message = messageService.sendMessage(
                    sender.getUserId(),
                    recipient.getUserId(),
                    itemId,
                    content
            );

            response.put("success", true);
            response.put("message", "✅ Message sent successfully!");
            response.put("data", new MessageDTO(message));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "❌ Error sending message: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
