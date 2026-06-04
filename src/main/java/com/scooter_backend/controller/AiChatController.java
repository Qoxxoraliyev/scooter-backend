package com.scooter_backend.controller;

import com.scooter_backend.dto.user.adminChat;
import com.scooter_backend.service.ai.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*") // Front-end ulanishida CORS muammosi bo'lmasligi uchun
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * AI Chat bot bilan muloqot qilish va yaqin skuterlarni so'rash APIsi.
     * POST http://localhost:8080/api/v1/ai/chat
     */
    @PostMapping("/chat")
    public ResponseEntity<String> chatWithAi(@RequestBody adminChat request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body("Xabar matni bo'sh bo'lishi mumkin emas!");
        }

        String response = aiChatService.generateAiResponse(request.message());
        return ResponseEntity.ok(response);
    }
}
