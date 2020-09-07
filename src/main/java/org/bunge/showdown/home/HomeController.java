package org.bunge.showdown.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping
    public String index(ModelMap map) {
        return "home/index";
    }
}
