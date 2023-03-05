package com.example.QABulletinBoard.question.mapper;

import com.example.QABulletinBoard.question.Question;
import com.example.QABulletinBoard.question.QuestionPostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    // QuestionPostDto -> Question
    Question questionPostDtoToQuestion(QuestionPostDto questionPostDto);
}
