package com.example.service;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository msgRepo;
    private final AccountRepository acctRepo;

    public MessageService(MessageRepository msgRepo, AccountRepository acctRepo) {
        this.msgRepo = msgRepo;
        this.acctRepo = acctRepo;
    }

    // Creating messages and returning save messages
    public Message create(Message m) {
        if (m == null
                || m.getMessageText() == null
                || m.getMessageText().isBlank()
                || m.getMessageText().length() > 255
                || m.getPostedBy() == null
                || !acctRepo.existsById(m.getPostedBy())) {
            throw new IllegalArgumentException("Invalid message payload");
        }
        return msgRepo.save(m);
    }

    public List<Message> getAll() {
        return msgRepo.findAll();
    }

    public Optional<Message> getOne(Integer id) {
        return msgRepo.findById(id);
    }

    // Return 1 if deleted and 0 if null
    public int delete(Integer id) {
        if (!msgRepo.existsById(id)) return 0;
        msgRepo.deleteById(id);
        return 1;
    }

    // Updating the text and returning 1 on success and throws on missing id
    public int updateText(Integer id, String newText) {
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            throw new IllegalArgumentException("Invalid message text");
        }
        Message existing = msgRepo.findById(id).orElse(null);
        if (existing == null) {
            throw new IllegalArgumentException("Message id not found");
        }
        existing.setMessageText(newText);
        msgRepo.save(existing);
        return 1;
    }

    public List<Message> getByAccount(Integer accountId) {
        return msgRepo.findByPostedBy(accountId);
    }
}
