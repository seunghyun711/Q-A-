package com.example.QABulletinBoard.question;

import com.example.QABulletinBoard.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
public class QuestionDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter

    public static class Post{
        @Positive
        private long memberId;

        @Valid
        @NotNull(message = "제목을 입력해주세요.")
        private String title; // 질문 게시글 제목

        @Valid
        @NotNull(message = "내용을 입력해주세요.")
        private String content; // 질문 내용

        @Valid
        @NotNull(message = "글의 공개범위를 선택하세요.")
        private boolean secretOrPublic;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setSecretOrPublic(boolean secretOrPublic) {
            this.secretOrPublic = secretOrPublic;
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Patch{
        @Positive
        private long questionId;

        @Positive
        private long memberId;

        @Valid
        @NotNull(message = "제목을 입력해주세요.")
        private String title; // 질문 게시글 제목

        @Valid
        @NotNull(message = "내용을 입력해주세요.")
        private String content; // 질문 내용

        @Valid
        @NotNull(message = "글의 공개범위를 선택하세요.")
        private boolean secretOrPublic;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private long questionId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String title;
        private String content;
        private long views;
        private Question.QuestionStatus questionStatus;
        private Question.SecretStatus secretStatus;
        private Member member;
    }

}
