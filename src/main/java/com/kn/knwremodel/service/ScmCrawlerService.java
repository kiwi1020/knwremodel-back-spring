package com.kn.knwremodel.service;

import com.kn.knwremodel.entity.ScmOcrEntity;
import com.kn.knwremodel.entity.ScmEntity;
import com.kn.knwremodel.repository.ScmOcrRepository;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScmCrawlerService {
    private static String News_URL = "https://web.kangnam.ac.kr/menu/board/info/ddc681caea557950be41fc172d7b8142.do?scrtWrtiYn=false&encMenuSeq=3b97657335d025de913a940dc19fa6b8&encMenuBoardSeq=a1eaaa881eb65b16940e8ac67d561498";

    private final ScmOcrRepository ocrTextRepository;

    public ScmCrawlerService(ScmOcrRepository ocrTextRepository) {
        this.ocrTextRepository = ocrTextRepository;
    }

    public List<ScmEntity> getNewsDatas() throws IOException {
        List<ScmEntity> newsList = new ArrayList<>();
        File tempDirectory = new File("C:\\Users\\ehdgu\\IdeaProjects\\testproject\\src\\main\\resources\\ScmDownloadImage"); // OCR 임시 이미지 저장 경로

        Document document = Jsoup.connect(News_URL).get();
        Elements pElements = document.select("ul p img");

        for (Element pElement : pElements) {
            String imageUrl = pElement.attr("abs:src");
            ScmEntity news = ScmEntity.builder()
                    .image(imageUrl)
                    .build();
            newsList.add(news);

            // OCR 처리
            File imageFile = downloadImage(imageUrl, tempDirectory.getAbsolutePath());
            String extractedText = performOCR(imageFile);
            saveOCRText(extractedText);
        }

        return newsList;
    }

    public File downloadImage(String imageUrl, String destinationPath) throws IOException {
        // 이미지를 다운로드할 경로 및 파일명 설정
        String fileName = "ScmImg.jpg"; // 원하는 파일명으로 수정
        File imageFile = new File(destinationPath, fileName);

        // 이미지 다운로드
        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream();
             OutputStream out = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return imageFile;
    }

    public String performOCR(File imageFile) {
        ITesseract tesseract = new Tesseract();
        try {
            // Tesseract의 트레이닝 데이터 경로 설정 (실제 데이터 경로로 수정 필요)
            tesseract.setDatapath("C:\\Users\\ehdgu\\IdeaProjects\\testproject\\src\\main\\resources\\tessdata");

            // 언어 설정
            tesseract.setLanguage("chi_tra");

            // 이미지 입력 설정
            tesseract.setPageSegMode(1); // 페이지 분할 모드 설정

            // 이미지를 BufferedImage로 읽어온다.
            BufferedImage image = ImageIO.read(imageFile);

            // 이미지의 해상도 설정
            tesseract.setTessVariable("user_defined_dpi", "300");


            return tesseract.doOCR(imageFile);
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return "";
        }
    }

    @Transactional
    public void saveOCRText(String extractedText) {
        ScmOcrEntity ocrText = new ScmOcrEntity();
        ocrText.setText(extractedText);
        ocrTextRepository.save(ocrText);
    }
}