package com.notionAuto.notionAuto.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/home")
    public String showHomepage()
    {

        return "home";
    }
}
