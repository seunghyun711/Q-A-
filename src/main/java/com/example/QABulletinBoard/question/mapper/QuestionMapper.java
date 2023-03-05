package com.example.QABulletinBoard.question.mapper;

import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.question.Question;
import com.example.QABulletinBoard.question.QuestionPostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    // QuestionPostDto -> Question
    default Question questionPostDtoToQuestion(QuestionPostDto questionPostDto){ // 자동 매핑하면 Member의 memberId를 가져올 수 없기 때문에 직접 매핑한다.
        Question question = new Question();
        Member member = new Member();
        member.setMemberId(questionPostDto.getMemberId());
        question.setTitle(questionPostDto.getTitle());
        question.setContent(questionPostDto.getContent());

        question.setMember(member);
        return question;
    }
}
