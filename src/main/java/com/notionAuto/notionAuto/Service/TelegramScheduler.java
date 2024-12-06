package com.notionAuto.notionAuto.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TelegramScheduler {
    private final TelegramBotService telegramBotService;
    private final NotionService notionService;
    @Autowired
    public TelegramScheduler(TelegramBotService telegramBotService,NotionService notionService) {
        this.telegramBotService = telegramBotService;
        this.notionService=notionService;
    }

    // 특정 사용자 Chat ID (텔레그램 봇과 대화하여 Chat ID 확인)
    private final Long chatId = 7475601411L; // 대상 사용자 ID

    @Scheduled(fixedRate = 360000) // 매 1시간마다 실행 (단위: 밀리초)
    public void sendPeriodicMessage() throws Exception {
        String newNotionPage=notionService.getNewNotionPage("13590dd6c2ce80048324c79dcdf5ed32");
        String response=notionService.getNotionPageChange(newNotionPage);
        notionService.saveNotionPage(newNotionPage);
        telegramBotService.sendMessage(chatId, response);
    }
}