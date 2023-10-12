package com.kn.knwremodel.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notices")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private Long boardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String regdate;

    @Column(nullable = false)
    private int view;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String body;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String img;

    @OneToMany(mappedBy = "notice", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc") // 댓글 정렬
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Notice(Long boardId, String title, String type, String major, String writer,
                  String regdate, int view, String body, String img) {
        this.boardId = boardId;
        this.title = title;
        this.type = type;
        this.major = major;
        this.writer = writer;
        this.regdate = regdate;
        this.view = view;
        this.body = body;
        this.img = img;
        this.comments = getComments();
    }
}
