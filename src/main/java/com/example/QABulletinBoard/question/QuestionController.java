package com.example.QABulletinBoard.question;

import com.example.QABulletinBoard.dto.SingleResponseDto;
import com.example.QABulletinBoard.question.mapper.QuestionMapper;
import com.example.QABulletinBoard.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@Validated
@RequestMapping("/QA/questions")
public class QuestionController {
    private final static String QUESTION_DEFAULT_URL = "/QA/questions";
    private final QuestionService questionService;
    private final QuestionMapper mapper;

    public QuestionController(QuestionService questionService, QuestionMapper mapper) {
        this.questionService = questionService;
        this.mapper = mapper;
    }

    // 질문 등록
    @PostMapping
    public ResponseEntity createQuestion(@Valid @RequestBody QuestionDto.Post questionPostDto) {
        Question question = mapper.questionPostDtoToQuestion(questionPostDto);
        questionService.createQuestion(question);
        URI location = UriCreator.createUri(QUESTION_DEFAULT_URL, question.getQuestionId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity patchQuestion(@PathVariable("question-id") @Positive long questionId,
                                         @Valid @RequestBody QuestionDto.Patch questionPatchDto) {
        questionPatchDto.setQuestionId(questionId);
        Question question = questionService.updateQuestion(mapper.questionPatchDtoToQuestion(questionPatchDto));

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.questionToQuestionResponse(question)),
                HttpStatus.OK
        );
    }

    // 게시글 단건 조회
    @GetMapping("/{question-id}")
    public ResponseEntity getQuestion(@PathVariable("question-id") @Positive long questionId,
                                      @Positive @RequestParam long memberId) {
        Question findQuestion = questionService.findQuestion(questionId, memberId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.questionToQuestionResponse(findQuestion)),
                HttpStatus.OK
        );
    }
}
