package com.example.QABulletinBoard.question;

import com.example.QABulletinBoard.exception.BusinessLogicException;
import com.example.QABulletinBoard.exception.ExceptionCode;
import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.member.MemberRepository;
import com.example.QABulletinBoard.member.MemberService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public QuestionService(QuestionRepository questionRepository, MemberService memberService, MemberRepository memberRepository) {
        this.questionRepository = questionRepository;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    /*
     질문 등록
     1. 질문을 작성한 회원의 email을 확인
        1-1. 유효한 회원인지 확인
        MANAGER인 경우 예외 발생(질문 등록 불가)
        MEMBER인 경우 질문 등록
     */
    public Question createQuestion(Question question) {
        // memberId에 해당하는 회원이 존재하는지 확인
        memberService.findVerifiedMember(question.getMember().getMemberId());
        // 질문 등록한 회원의 이메일 검증(관리자 계정? or 일반 회원?)
        checkMemberRole(question.getMember().getMemberId());
        return questionRepository.save(question);
    }

    /*
     질문 수정
     1. 수정하려는 게시글이 있는지 확인
     2. 수정
     */
    public Question updateQuestion(Question question) {
        // 1. 수정하려는 게시글이 있는지 확인
        Question findQuestion = findVerifiedQuestion(question);

        // 수정하려는 회원과 게시글을 작성한 회원이 동일한지 확인
        if (question.getMember().getMemberId() != findQuestion.getMember().getMemberId()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_MATCHED);
        }

        // 2. 수정
        Optional.ofNullable(question.getTitle())
                .ifPresent(title -> question.setTitle(title));
        Optional.ofNullable(question.getContent())
                .ifPresent(content -> question.setContent(content));
        Optional.ofNullable(question.getSecretStatus())
                .ifPresent(secretStatus -> question.setSecretStatus(secretStatus));

        return questionRepository.save(question);

    }

    /*
    질문 단건 조회
    1. 조회하려는 회원이 관리자이거나 질문글 작성자인지 확인
     */
//    public Question findQuestion(Question question){
//
//    }

    private void checkMemberRole(long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.get().getMemberId() == 1) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_REGISTRATION_QUESTION);
        }
    }

    public Question findVerifiedQuestion(Question question) {
        Optional<Question> optionalQuestion =
                questionRepository.findById(question.getQuestionId());
        Question findQuestion =
                optionalQuestion.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        return findQuestion;

    }

}
