package com.example.QABulletinBoard.question;

import com.example.QABulletinBoard.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long questionId; // id

    @Column(nullable = false)
    private String title; // 게시판 제목

    @Column(nullable = false)
    private String content; // 게시글 내용

    @Column(nullable = false)
    private LocalDateTime created_At = LocalDateTime.now(); // 게시글 작성 시간

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTRATION; // 질문 게시글 상태

    @ManyToOne // 다대일 매핑 : Question(N) - Member(1)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void addMember(Member member) {
        this.member = member;
    }

    public enum QuestionStatus{
        QUESTION_REGISTRATION("질문 등록 상태"),
        QUESTION_ANSWERED("답변 완료 상태"),
        QUESTION_DELETE("질문 삭제 상태");

        @Getter
        private String status;

        QuestionStatus(String status) {
            this.status = status;
        }
    }
}
