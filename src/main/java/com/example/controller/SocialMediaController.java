package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

// This class listens for HTTP requests and sends back JSON responses.
 @RestController
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    // Constructor to inject services

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    // User Stories 1 and 2 
    /**
     * POST /register
     * Make a new account.
     * - 200 with the saved Account on success
     * - 409 if the username already exists
     * - 400 if username blank or password too short
     */

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account body) {
        try {
            Account saved = accountService.register(body);
            return ResponseEntity.ok(saved);
        } catch (AccountService.DuplicateUsernameException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /login
     * Check username + password.
     * - 200 with Account if correct
     * - 401 if wrong
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account body) {
        Account found = accountService.login(body.getUsername(), body.getPassword());
        if (found == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(found);
    }

    // User Stories 3 to 8
    /**
     * POST /messages
     * Create a new message.
     * - 200 with Message on success
     * - 400 if text blank/too long or user does not exist
     */

    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message body) {
        try {
            Message saved = messageService.create(body);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /messages
     * Get every message.
     * Always 200 (list can be empty)
     */

    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageService.getAll();
    }

    /**
     * GET /messages/{messageId}
     * Get one message by id.
     * 200 with the message if found
     * 200 with EMPTY BODY if not found
     */

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessage(@PathVariable Integer messageId) {
        return messageService.getOne(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok().build()); 
    }

    /**
     * DELETE /messages/{messageId}
     * Delete a message by id.
     * 200 with body 1 if something was deleted
     * 200 with EMPTY BODY if id did not exist
     */

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer messageId) {
        int rows = messageService.delete(messageId);
        if (rows == 1) {
            return ResponseEntity.ok(rows);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH /messages/{messageId}
     * Update ONLY the message text.
     * Request JSON: { "messageText": "new text" }
     * 200 with body 1 if updated
     * 400 if id not found OR text blank/too long
     */

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer messageId,
                                           @RequestBody Map<String, Object> body) {
        try {
            String newText = (String) body.get("messageText");
            int rows = messageService.updateText(messageId, newText);
            return ResponseEntity.ok(rows);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /accounts/{accountId}/messages
     * Get all messages posted by one user.
     * Always 200
     */

    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getMessagesByAccount(@PathVariable Integer accountId) {
        return messageService.getByAccount(accountId);
    }
}