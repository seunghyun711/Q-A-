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

    private void checkMemberRole(long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.get().getMemberId() == 1) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_REGISTRATION_QUESTION);
        }
    }
}
