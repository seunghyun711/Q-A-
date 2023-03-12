package com.example.QABulletinBoard.question.mapper;

import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.question.Question;
import com.example.QABulletinBoard.question.QuestionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    // QuestionPostDto -> Question
    default Question questionPostDtoToQuestion(QuestionDto.Post questionPostDto){ // 자동 매핑하면 Member의 memberId를 가져올 수 없기 때문에 직접 매핑한다.
        Question question = new Question();
        Member member = new Member();
        member.setMemberId(questionPostDto.getMemberId());
        question.setTitle(questionPostDto.getTitle());
        question.setContent(questionPostDto.getContent());

        if (questionPostDto.isSecretOrPublic()) {
            question.setSecretStatus(Question.SecretStatus.SECRET); // 비밀 글
        }else{
            question.setSecretStatus(Question.SecretStatus.PUBLIC); // 공개 글
        }

        question.setMember(member);
        return question;
    }

    default Question questionPatchDtoToQuestion(QuestionDto.Patch questionPatchDto){
        Question question = new Question();
        question.setMember(new Member());
        question.setQuestionId(questionPatchDto.getQuestionId());
        question.getMember().setMemberId(questionPatchDto.getMemberId());
        question.setTitle(questionPatchDto.getTitle());
        question.setContent(questionPatchDto.getContent());

        // 비밀글 여부 설정
        if(questionPatchDto.isSecretOrPublic()){
            question.setSecretStatus(Question.SecretStatus.SECRET); // 비밀글 설정
        }else{
            question.setSecretStatus(Question.SecretStatus.PUBLIC); // 공개글 설정
        }
        return question;
    }

    @Mapping(source = "member.memberId",target = "memberId")
    QuestionDto.Response questionToQuestionResponse(Question question);
}
