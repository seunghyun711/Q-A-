package com.example.QABulletinBoard.member;

import com.example.QABulletinBoard.member.mapper.MemberMapper;
import com.example.QABulletinBoard.question.Question;
import com.example.QABulletinBoard.question.QuestionDto;
import com.example.QABulletinBoard.question.QuestionService;
import com.example.QABulletinBoard.question.mapper.QuestionMapper;
import com.example.QABulletinBoard.utils.UriCreator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

import static com.example.QABulletinBoard.question.QuestionController.QUESTION_DEFAULT_URL;

@RestController
@Validated
@RequestMapping(value = "/QA/members")
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/QA/members";
    private final MemberService memberService;
    private final MemberMapper mapper;
    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    public MemberController(MemberService memberService,
                            MemberMapper mapper,
                            QuestionService questionService,
                            QuestionMapper questionMapper) {
        this.memberService = memberService;
        this.mapper = mapper;
        this.questionService = questionService;
        this.questionMapper = questionMapper;
    }
    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberPostDto memberPostDto) {
        Member member = mapper.memberPostDtoToMember(memberPostDto);
        memberService.createMember(member);
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());

        return ResponseEntity.created(location).build();
    }

    // 특정 회원이 질문을 등록하기 때문에 MemberController에서 질문 등록
    @PostMapping("/{member_id}/questions")
    public ResponseEntity postQuestion(@Positive @PathVariable("member_id") long memberId,
                                       @Valid @RequestBody QuestionDto.Post questionPostDto) {
        questionPostDto.addMemberId(memberId);
        Question question = questionMapper.questionPostDtoToQuestion(questionPostDto);
        questionService.createQuestion(question);
        URI location = UriCreator.createUri(QUESTION_DEFAULT_URL, question.getQuestionId());

        return ResponseEntity.created(location).build();
    }


}
