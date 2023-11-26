package com.kn.knwremodel.dto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class KeywordDTO {
    @Getter
    @AllArgsConstructor
    public static class request{
        private String keyword;
        private HttpServletRequest httpServletRequest;

    }

    @Getter
    @AllArgsConstructor
    public static class requestRecentlyKeyword{
        private String keyword;
        private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;

    }



}