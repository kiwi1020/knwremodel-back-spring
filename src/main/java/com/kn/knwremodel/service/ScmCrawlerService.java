package com.kn.knwremodel.service;
import com.kn.knwremodel.entity.ScmEntity;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ScmCrawlerService {
    private static String News_URL = "https://web.kangnam.ac.kr/menu/board/info/ddc681caea557950be41fc172d7b8142.do?scrtWrtiYn=false&encMenuSeq=3b97657335d025de913a940dc19fa6b8&encMenuBoardSeq=a1eaaa881eb65b16940e8ac67d561498";

    @PostConstruct
    public List<ScmEntity.News> getNewsDatas() throws IOException {
        List<ScmEntity.News> newsList = new ArrayList<>();
        Document document = Jsoup.connect(News_URL).get();

        // <ul> 요소 내부의 <p> 태그 안에 있는 이미지 URL만 추출
        Elements pElements = document.select("ul p img");

        for (Element pElement : pElements) {
            String imageUrl = pElement.attr("abs:src"); // 이미지 URL을 절대 URL로 추출
            ScmEntity.News news = ScmEntity.News.builder()
                    .image(imageUrl) // 이미지
                    .build();
            newsList.add(news);
        }

        return newsList;
    }
}