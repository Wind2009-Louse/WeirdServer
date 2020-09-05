package com.weird.handler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeHandler {

    /**
     * 首页
     *
     * @return 首页信息
     */
    @RequestMapping({"/index.html", "/"})
    public String Homepage_html(){
        return "Hello!\n你好！\nこんにちは";
    }
}
