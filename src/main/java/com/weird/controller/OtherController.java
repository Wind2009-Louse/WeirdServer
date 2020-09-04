package com.weird.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OtherController {
    @RequestMapping({"/index.html", "/"})
    String Homepage_html(){
        return "Hello!\n你好！\nこんにちは";
    }
}
