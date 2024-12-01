package com.notionAuto.notionAuto.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TelegramBotService {
    @Value("${telegram.api.token}")
    private String BOT_TOKEN;

    private final WebClient webClient;

    public TelegramBotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.telegram.org")
                .build();
    }

    public void sendMessage(Long chatId, String message) {
        String sendMessageEndpoint = "/bot" + BOT_TOKEN + "/sendMessage";

        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(sendMessageEndpoint)
                        .queryParam("chat_id", chatId)
                        .queryParam("text", message)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> System.out.println("Message sent: " + response),
                        error -> System.err.println("Failed to send message: " + error.getMessage())
                );
    }
}