package com.example.QABulletinBoard.answer;

import com.example.QABulletinBoard.answer.mapper.AnswerMapper;
import com.example.QABulletinBoard.utils.UriCreator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@Validated
@RequestMapping("/QA/Answers")
public class AnswerController {
    private final static String ANSWER_DEFAULT_URL = "/QA/Answers";
    private final AnswerService answerService;
    private final AnswerMapper mapper;

    public AnswerController(AnswerService answerService, AnswerMapper mapper) {
        this.answerService = answerService;
        this.mapper = mapper;
    }

    // 답변 등록
    @PostMapping
    public ResponseEntity postAnswer(@Valid @RequestBody AnswerDto.Post post) {
        Answer answer = answerService.createAnswer(mapper.answerPostDtoToAnswer(post));
        URI location = UriCreator.createUri(ANSWER_DEFAULT_URL, answer.getAnswerId());

        return ResponseEntity.created(location).build();
    }
}
