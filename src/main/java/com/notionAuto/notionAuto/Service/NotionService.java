package com.notionAuto.notionAuto.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notionAuto.notionAuto.Entity.NotionPage;
import com.notionAuto.notionAuto.Repository.NotionPageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class NotionService {
    @Value("${notion.api.token}")
    private String NOTION_API_TOKEN;
    private final GptService gptService;
    private final NotionPageRepository notionPageRepository;



    private final WebClient webClient = WebClient.create("https://api.notion.com/v1");

    public NotionService(GptService gptService, NotionPageRepository notionPageRepository) {
        this.gptService = gptService;
        this.notionPageRepository = notionPageRepository;
    }

    public String getNewNotionPage(String pageId) throws Exception {
        String jsonString = webClient.get()
                .uri("/blocks/" + pageId + "/children")
                .header("Authorization", "Bearer " + NOTION_API_TOKEN)
                .header("Notion-Version", "2022-06-28")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info(jsonString);

        // ObjectMapper를 사용하여 JSON을 JsonNode로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        StringBuilder sb = new StringBuilder();
        // "results" 배열을 가져오기
        JsonNode results = jsonNode.get("results");

        // 각 블록을 순차적으로 처리
        for (JsonNode block : results) {
            if (block.has("paragraph")) {  // 'paragraph' 키가 존재하면 처리
                JsonNode paragraph = block.get("paragraph");
                JsonNode richTextArray = paragraph.get("rich_text");

                for (JsonNode textNode : richTextArray) {
                    String plainText = textNode.get("plain_text").asText();
                    sb.append(plainText).append("\n");
                }
            }
            if (block.has("bulleted_list_item")) {  // 'bulleted_list_item' 처리
                JsonNode bulletedListItem = block.get("bulleted_list_item");
                JsonNode richTextArray = bulletedListItem.get("rich_text");

                for (JsonNode textNode : richTextArray) {
                    String plainText = textNode.get("plain_text").asText();
                    sb.append("- ").append(plainText).append("\n");  // 목록 항목 추가
                }
            }
           if (block.has("child_page")) { // 하위 페이지 탐색
                String childPageId = block.get("id").asText();
                String childPageTitle = block.get("child_page").get("title").asText();
                sb.append("== Child Page: ").append(childPageTitle).append(" ==\n");

                // 재귀적으로 하위 페이지의 내용 가져오기
                sb.append(getNewNotionPage(childPageId));
            }
           if(block.has("code"))
           {
               JsonNode code=block.get("code");
               JsonNode richTextArray=code.get("rich_text");
               for (JsonNode textNode : richTextArray) {
                   String plainText = textNode.get("plain_text").asText();
                   sb.append("code ").append(plainText).append("\n");  // 목록 항목 추가
               }
           }
        }
        return sb.toString();
    }

    public NotionPage saveNotionPage(String content) {
        NotionPage notionPage = new NotionPage(content);
        return notionPageRepository.save(notionPage);
    }

    public String getNotionPageChange(String newNotionPage) {
        NotionPage lastNotionPage = notionPageRepository.findTop1ByOrderByIdDesc();
        log.info("lastNotionPage={},NewNotionPage={}",lastNotionPage.getContent(),newNotionPage);
        return gptService.getGPTSummary(lastNotionPage.getContent(), newNotionPage);

    }
}