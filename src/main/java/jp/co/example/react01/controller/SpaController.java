package jp.co.example.react01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({ "/", "/login", "/login-result", "/list" })
    public String index() {
        return "forward:/index.html";
    }
}
