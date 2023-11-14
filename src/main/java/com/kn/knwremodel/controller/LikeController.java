package com.kn.knwremodel.controller;

import com.kn.knwremodel.dto.LikeDTO;
import com.kn.knwremodel.entity.Notice;
import com.kn.knwremodel.service.LikeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/like")
@RestController
public class LikeController {
    private final LikeService likeS;

    @PostMapping("/click")
    public ResponseEntity clickLike(@RequestBody LikeDTO.click dto) throws Exception {
        return ResponseEntity.ok(likeS.clickLike(dto));
    }


}