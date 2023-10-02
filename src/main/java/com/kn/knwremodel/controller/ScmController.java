package com.kn.knwremodel.controller;

import com.kn.knwremodel.entity.ScmEntity;
import com.kn.knwremodel.service.ScmCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@Controller
public class ScmController {

    private final ScmCrawlerService CrawlerService;

    @Autowired
    public ScmController(ScmCrawlerService CrawlerService) {
        this.CrawlerService = CrawlerService;
    }

    @GetMapping("/news")
    public String news(Model model) throws Exception {
        List<ScmEntity> newsList = CrawlerService.getNewsDatas();
        model.addAttribute("news", newsList);

        return "news";
    }
}