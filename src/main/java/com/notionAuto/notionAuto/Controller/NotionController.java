package com.notionAuto.notionAuto.Controller;

import com.notionAuto.notionAuto.Service.GptService;
import com.notionAuto.notionAuto.Service.NotionService;
import com.notionAuto.notionAuto.Service.TelegramScheduler;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotionController {


    private final NotionService notionService;
    private final GptService gptService;
    private final TelegramScheduler telegramScheduler;

    public NotionController(NotionService notionService, GptService gptService, TelegramScheduler telegramScheduler) {
        this.notionService = notionService;
        this.gptService = gptService;
        this.telegramScheduler = telegramScheduler;
    }


    @PostMapping("/track")
    public String trackPageChanges(Model model) throws Exception {
        String newNotionPage=notionService.getNewNotionPage("13590dd6c2ce80048324c79dcdf5ed32");
        String response=notionService.getNotionPageChange(newNotionPage);
        notionService.saveNotionPage(newNotionPage);
        telegramScheduler.sendPeriodicMessage(response);
        model.addAttribute("notionResponse",response);
        return "home";

        }


    }

