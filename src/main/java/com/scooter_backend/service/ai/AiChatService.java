package com.scooter_backend.service.ai;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiChatService {

    private final ChatModel chatModel;

    public AiChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generateAiResponse(String userMessage) {
        // AI ga uning roli va vazifasini tushuntiramiz (System Prompt)
        String systemInstructions = """
                Siz "Scooter-Backend" tizimining aqlli va xushmuomala yordamchisiz.
                Foydalanuvchilarga skuterlar haqidagi savollarga javob bering.
                
                MUHIM QOIDA:
                Agar foydalanuvchi o'ziga eng yaqin skuterni topib berishni so'rasa va o'zining koordinatalarini 
                (latitude va longitude) matnda ko'rsatsa, siz darhol `nearestScooterFunction` funksiyasini chaqiring.
                Funksiyadan ma'lumot qaytganidan so'ng, foydalanuvchiga skuter nomi, masofasi va batareya quvvatini 
                chiroyli, tushunarli formatda o'zbek tilida bayon qiling.
                """;

        // `NearestScooterService`ni modelga asbob (Tool) sifatida ulaymiz
        // @Service("nearestScooterFunction") deb e'lon qilgan nomingiz bilan mos bo'lishi shart
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withFunction("nearestScooterFunction")
                .build();

        // Xabarlarni yig'ib prompt yaratamiz
        Prompt prompt = new Prompt(
                List.of(new SystemMessage(systemInstructions), new UserMessage(userMessage)),
                options
        );

        // OpenAI ga so'rov yuboramiz va matnli javobni qaytaramiz
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}