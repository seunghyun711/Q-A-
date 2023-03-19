package com.example.QABulletinBoard.answer;

import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
public class AnswerDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Post{
        @Positive
        private long questionId;
        @Positive
        private long memberId;
        @NotBlank(message = "제목을 입력해주세요")
        private String title;
        @NotBlank(message = "답변을 입력해 주세요.")
        private String content;


    }
}
