package com.example.QABulletinBoard.member;

import com.example.QABulletinBoard.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBER;

    @OneToMany(mappedBy = "member") // Question - Member 간 다대일 양방향 매핑
    private List<Question> questions = new ArrayList<>();

    public enum Role{
        MEMBER("일반 회원"),
        MANAGER("관리자");

        @Getter
        private String status;

        Role(String status) {
            this.status = status;
        }
    }
}
