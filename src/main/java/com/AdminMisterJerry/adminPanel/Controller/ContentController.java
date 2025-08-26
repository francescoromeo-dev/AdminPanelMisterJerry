package com.AdminMisterJerry.adminPanel.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//Controlla le richieste http e mostra le pagine html
@Controller
public class ContentController {
    
    @GetMapping("/req/login")
    private String login(){
        return "login";
    }

    @GetMapping("/admin-panel")
    private String adminPanel(){
        return "admin-panel";
    }

}
