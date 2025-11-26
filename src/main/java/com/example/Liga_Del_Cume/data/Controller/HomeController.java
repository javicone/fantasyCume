package com.example.Liga_Del_Cume.data.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    public HomeController() {
        // TODO Auto-generated constructor stub
        System.out.println("\t Builder of " + this.getClass().getSimpleName());
    }

    @GetMapping("/")
    public String index() {
        System.out.println("\t Peticion GET de index /");
        return "index";
    }
}