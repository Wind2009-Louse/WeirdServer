package com.weird.handler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeHandler {
    @RequestMapping({"/index.html", "/"})
    String Homepage_html(){
        return "Hello!\n你好！\nこんにちは";
    }
}
