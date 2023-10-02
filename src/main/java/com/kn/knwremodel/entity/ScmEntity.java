package com.kn.knwremodel.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ScmEntity {

    @Getter
    @Builder
    @ToString
    public static class News {
        private String image;

        public String getImage() {
            return image;
        }
    }
}
