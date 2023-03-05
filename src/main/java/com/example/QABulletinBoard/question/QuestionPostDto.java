package com.example.QABulletinBoard.question;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class QuestionPostDto {
    @Positive
    private long memberId;

    @Valid
    @NotNull(message = "제목을 입력해주세요.")
    private String title; // 질문 게시글 제목

    @Valid
    @NotNull(message = "내용을 입력해주세요.")
    private String content; // 질문 내용
}
