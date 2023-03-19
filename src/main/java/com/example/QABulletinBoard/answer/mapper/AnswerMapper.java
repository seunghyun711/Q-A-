package com.example.QABulletinBoard.answer.mapper;

import com.example.QABulletinBoard.answer.Answer;
import com.example.QABulletinBoard.answer.AnswerDto;
import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.question.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
   @Mapping(source = "memberId",target = "member.memberId")
   @Mapping(source = "questionId",target = "question.questionId")
   Answer answerPostDtoToAnswer(AnswerDto.Post answerPostDto);

   @Mapping(source = "memberId",target = "member.memberId")
   @Mapping(source = "questionId",target = "question.questionId")
   Answer answerPatchDtoToAnswer(AnswerDto.Patch answerPatchDto);

}
