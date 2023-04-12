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
    @Setter
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

        public void addMemberId(long memberId){
            this.memberId = memberId;
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Patch{
        @Positive
        private long questionId;

        @Positive
        @Valid
        @NotNull(message = "회원 id를 입력하세요")
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

        public void setQuestionId(long questionId) {
            this.questionId = questionId;
        }

        public Member getMember(){
            Member member = new Member();
            member.setMemberId(memberId);
            return member;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        @Positive
        private long questionId;
        @Positive
        private long memberId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String title;
        private String content;
        private long views;
        private Question.QuestionStatus questionStatus;
        private Question.SecretStatus secretStatus;
    }

}
