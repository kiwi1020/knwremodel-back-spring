package com.kn.knwremodel.service;

import java.io.IOException;
import java.util.List;

import org.hibernate.query.sqm.tree.domain.SqmIndexAggregateFunction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kn.knwremodel.dto.CommentDTO.save;
import com.kn.knwremodel.entity.College;
import com.kn.knwremodel.entity.Notice;
import com.kn.knwremodel.repository.CollegeRepository;
import com.kn.knwremodel.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository noticeRepo;
    private final CollegeRepository CollegeRepo;
    private int maxPage = 1; //크롤링할 공지사항 페이지의 수
    @Transactional
    public void updata() {
        List<Notice> notices = noticeRepo.findAll();
        JSONParser parser = new JSONParser();

        try {
            for (College e : CollegeRepo.findAll()) {
                if (!noticeRepo.existsByMajor(e.getMajor()) || !(noticeRepo.findMaxBoardIdByMajor(e.getMajor()) == first_Notice_id(e.getUrl()))) {
                    loopout:

                    for (int page = 1; page <= maxPage; page++) {
                        Document document = Jsoup.connect(e.getUrl() + "?paginationInfo.currentPageNo=" + page).get();
                        Elements contents = document.getElementsByClass("tbody").select("ul");

                        for (Element content : contents) {
                            String columnNumber = content.select("li").first().text();

                            if (columnNumber.equals("필독") || columnNumber.equals("공지")) {
                                continue;
                            }

                            Long id = Long.parseLong(columnNumber);
                            Elements titlElements = content.select("a");

                            if (noticeRepo.existsByBoardId(id)){
                                break loopout;
                            }

                            //게시물 내용, 사진 크롤링
                            JSONObject jsonObject = (JSONObject)parser.parse(titlElements.attr("data-params"));
                            String encMenuSeq = (String) jsonObject.get("encMenuSeq");
                            String encMenuBoardSeq = (String) jsonObject.get("encMenuBoardSeq");

                            int index = e.getUrl().indexOf("menu") + 4;
                            String articleURL = e.getUrl().substring(0, index) + "/board/info" + e.getUrl().substring(index) +
                                    "?scrtWrtiYn=false&encMenuSeq=" + encMenuSeq + "&encMenuBoardSeq=" + encMenuBoardSeq;


                            Document articleDocument = Jsoup.connect(articleURL).get();
                            Elements articleContents = articleDocument.getElementsByClass("tbody").select("ul");
                            
                            String body = "";
                            for (Element i : articleContents.select("li p:not([style*='display:none'])")) {
                                String temp = i.text();
                                if (temp != ""){
                                    body += temp;
                                    body += "\n";
                                }
                        
                            }
                            // html 통째로 긁기 프론트엔드랑 회의 필요
                            // String post = articleContents.select("li p:not([style*='display:none'])").toString();


                            String img = "";
                            for (Element i : articleContents.select("img")) {
                                String temp = i.attr("abs:src");
                                // img tag inline data
                                if (temp.substring(0, 30).contains("data:image")){
                                    continue;
                                }else{
                                    img += temp + ";";
                                }
                            }


                            notices.add(new Notice(id,
                                    titlElements.text(),
                                    content.select("li.ali").text(),
                                    e.getMajor(),
                                    content.select("li.sliceDot6").text(),
                                    content.select("li.sliceDot6").next().text(),
                                    Integer.parseInt(content.select("li.sliceDot6").next().next().text().replace(",", "")),
                                    body,
                                    img,
                                    0L));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        noticeRepo.saveAll(notices);
    }

    private Long first_Notice_id(String url) throws IOException {
        Document document = Jsoup.connect(url + "?paginationInfo.currentPageNo=" + 1).get();
        Elements contents = document.getElementsByClass("tbody").select("ul");
        Long boardId = null;

        for (Element content : contents) {
            if (content.select("li").first().text().equals("필독") ||
                    content.select("li").first().text().equals("공지")) {
                continue;
            }

            boardId = Long.parseLong(content.select("li").first().text());
            break;
        }

        if (boardId == null) {
            throw new IOException();
        } else {
            return boardId;
        }
    }
    public Notice findById(Long id) {
        return noticeRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public List<Notice> findAll() {
        return noticeRepo.findAll();
    }

    public List<Notice> findByMajorContaining(String major) {
        return noticeRepo.findByMajorContaining(major);
    }

    public List<Notice> findByMajorAndType(String major, String type, Long count, Long page) {
        List<Notice> notices;
        if (major == null && type == null){
            notices = noticeRepo.findAll();
        }else if(major == null){
            notices = noticeRepo.findByType(type);
        }else if(type == null){
            notices = noticeRepo.findByMajor(major);
        }else{
            notices = noticeRepo.findByMajorAndType(major, type);
        }
         
        Long e = Math.min(count * page, notices.size());

        notices = notices.subList((int) (count * (page - 1)), e.intValue());
        return notices;
    }
}