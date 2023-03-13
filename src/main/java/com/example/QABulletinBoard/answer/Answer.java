package com.example.QABulletinBoard.answer;

import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.question.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long answerId; // id

    @Column(nullable = false)
    private String content; // 답변 내용

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 질문 작성 시간

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now(); // 질문 수정 시간

    public enum SecretStatus{
        PUBLIC("공개글"),
        SECRET("비밀글");

        @Getter
        private String status;

        SecretStatus(String status) {
            this.status = status;
        }
    }

}
