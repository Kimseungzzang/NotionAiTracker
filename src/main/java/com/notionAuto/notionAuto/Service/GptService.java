package com.notionAuto.notionAuto.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class GptService {
    @Value("${gpt.api.key}")
    private String apiKey ;
    public String getGPTSummary(String lastContent, String newContent) {
        WebClient webClient = WebClient.create("https://api.openai.com/v1/chat/completions");


        String prompt = "You must speak Korean. I will send you the existing and new contents for each employee, so please summarize the new and changed contents\n" + "This is last content" + lastContent + "this is new content" + newContent;

        // 요청 본문을 Map으로 만들어 전달
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        requestBody.put("max_tokens", 1000);

        // 메시지 배열 만들기
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        requestBody.put("messages", new Object[]{message});

        // JSON 객체를 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = null;
        try {
            jsonRequestBody = objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalJsonRequestBody = jsonRequestBody;
        String responseJson = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)  // JSON 형식으로 요청 본문 전달
                .retrieve()
                .bodyToMono(String.class)
                .block();  // 동기적으로 결과 받기

        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);
            return rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";

        }
    }
}


