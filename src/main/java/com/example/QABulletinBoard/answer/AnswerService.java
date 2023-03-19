package com.example.QABulletinBoard.answer;

import com.example.QABulletinBoard.exception.BusinessLogicException;
import com.example.QABulletinBoard.exception.ExceptionCode;
import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.member.MemberRepository;
import com.example.QABulletinBoard.member.MemberService;
import com.example.QABulletinBoard.question.Question;
import com.example.QABulletinBoard.question.QuestionRepository;
import com.example.QABulletinBoard.question.QuestionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    public AnswerService(AnswerRepository answerRepository, MemberRepository memberRepository, MemberService memberService,
                         QuestionRepository questionRepository, QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.questionRepository = questionRepository;
        this.questionService = questionService;
    }

    /*
            답변 등록
            1. 관리자만 가능해야 하므로 memberId를 통해 작성자의 role 확인
                1-1. 관리자면 답변 등록
                1-2. 일반 사용자면 예외 처리
            2. 답변 등록 시 게시글의 상태를 답변이 되었음으로 변경
             */
    public Answer createAnswer(Answer answer) {
        // 1. 관리자만 가능해야 하므로 memberId를 통해 작성자의 role 확인
        verifiedAnswer(answer);
//        if (answer.getQuestion().getSecretStatus().equals(Question.SecretStatus.SECRET)) {
//            answer.setSecretStatus(Answer.SecretStatus.SECRET);
//        }
        answerRepository.save(answer);

        // 2. 답변 등록 시 게시글의 상태를 답변이 되었음으로 변경
        setQuestionStatus(answer);
        return answer;
    }

    /*
    답변 수정
    1. 답변 수정자와 답변 작성자가 동일 인물인지 검증
    2. 수정
     */
    public Answer updateAnswer(Answer answer) {
        // 1. 답변 수정자와 답변 작성자가 동일 인물인지 검증
        Answer findAnswer = checkAnswerWriter(answer);

        // 2. 수정
        Optional.ofNullable(answer.getTitle()).ifPresent(title -> findAnswer.setTitle(title));
        Optional.ofNullable(answer.getContent()).ifPresent(content -> findAnswer.setContent(content));
        return answerRepository.save(findAnswer);
    }

    /*
    답변 삭제

     */
    public void deleteAnswer(long answerId, long memberId) {
        Answer answer = new Answer();
        answer.setAnswerId(answerId);

        Answer deleteAnswer = checkDeleteAnswer(answerId, memberId);
        answerRepository.delete(answer);
    }

    public void verifiedAnswer(Answer answer) {
        Optional<Question> optionalQuestion = questionRepository.findById(answer.getQuestion().getQuestionId());
        Question question = optionalQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        Optional<Member> optionalMember = memberRepository.findById(answer.getMember().getMemberId());
        Member member = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if (question.getSecretStatus().equals(Question.SecretStatus.SECRET)) {
            answer.setSecretStatus(Answer.AnswerSecretStatus.SECRET);
            if (question.getMember().getMemberId() != answer.getMember().getMemberId() && !member.getEmail().equals("admin@gmail.com")) {
                throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
            }
        }else{
            answer.setSecretStatus(Answer.AnswerSecretStatus.PUBLIC);
        }

    }

    private void setQuestionStatus(Answer answer) {
        Optional<Question> optionalQuestion = questionRepository.findById(answer.getQuestion().getQuestionId());
        Question question = optionalQuestion.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        if (question.getQuestionStatus().equals(Question.QuestionStatus.QUESTION_REGISTRATION)) {
            question.setQuestionStatus(Question.QuestionStatus.QUESTION_ANSWERED);
            questionRepository.save(question);
        }
    }

    private Answer checkAnswerWriter(Answer answer) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answer.getAnswerId());
        Answer findAnswer = optionalAnswer.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        Optional<Member> optionalMember = memberRepository.findById(answer.getMember().getMemberId());

        if (answer.getMember().getMemberId() != findAnswer.getAnswerId()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_MATCHED);
        }
        return findAnswer;
    }

    private Answer checkDeleteAnswer(long answerId, long memberId) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        Answer findAnswer = optionalAnswer.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (findAnswer.getMember().getMemberId() != memberId && !findMember.getEmail().equals("admin@gmain.com")) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
        return findAnswer;
    }


}
