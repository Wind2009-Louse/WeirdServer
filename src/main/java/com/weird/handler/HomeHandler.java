package com.weird.handler;

import com.weird.service.CardPreviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeHandler {
    @Autowired
    CardPreviewService cardPreviewService;

    /**
     * 首页
     *
     * @return 首页信息
     */
    @RequestMapping({"/weird_project/index.html", "/weird_project/"})
    public String homePage() {
        return "Hello!\n你好！\nこんにちは";
    }
}
