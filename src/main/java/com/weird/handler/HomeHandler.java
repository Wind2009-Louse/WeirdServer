package com.weird.handler;

import com.weird.service.CardDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeHandler {
    @Autowired
    CardDetailService cardDetailService;

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
