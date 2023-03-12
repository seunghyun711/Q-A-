package com.example.QABulletinBoard.question;

import com.example.QABulletinBoard.exception.BusinessLogicException;
import com.example.QABulletinBoard.exception.ExceptionCode;
import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.member.MemberRepository;
import com.example.QABulletinBoard.member.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;
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
        Question findQuestion = findVerifiedQuestion(question.getQuestionId());

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
    1. 해당 게시글을 작성한 회원이 존재하는지 확인
    2. 해당 게시글 존재하는지 확인
    2. 조회하려는 회원이 관리자이거나 질문글 작성자인지 확인
     */
    public Question findQuestion(long questionId, long memberId){
        // 1. 해당 게시글을 작성한 회원이 존재하는지 확인
        Member member = memberService.findVerifiedMember(memberId);

        // 2. 해당 게시글 존재하는지 확인
        Question findQuestion = findVerifiedQuestion(questionId);

        return checkAuthority(findQuestion, member);

    }

    /*
    질문 목록 조회
    1. 회원 검증
    2. 정렬기준에 따라서 페이지 정렬
     */
    public Page<Question> findQuestions(long memberId, int page, int size, String sortBy) {
        // 1, 회원 검증
        memberService.findVerifiedMember(memberId);

        // 2. 정렬기준에 따라 페이지 정렬
        if (sortBy.equals("new")) { // 최신순
            return questionRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else if (sortBy.equals("old")) { // 오래된 순
            return questionRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").ascending()));
        }

        // 기본 값은 최신순
        return questionRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    /*
    질문 삭제
    1. 회원 검증
    2. 질문 존재 여부 확인
    3. 질문을 삭제하려는 회원이 해당 질문의 작성자인지 확인
     */
    public void deleteQuestion(long questionId, long memberId) {
        // 1. 회원 검증
        memberService.findVerifiedMember(memberId);

        // 2. 질문 존재 여부 확인
        Question question = findVerifiedQuestion(questionId);

        // 3. 질문을 삭제하려는 회원이 해당 질문의 작성자인지 확인
        if (question.getMember().getMemberId() != memberId) { // 삭제하려는 회원과 작성자가 다른 경우
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }else{
            if (question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETE) { // 이미 게시글이 삭제된 경우
                throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
            }else{
                question.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETE); // 정상적으로 삭제된 경우 게시글을 삭제 상태로 변경
            }
        }
        questionRepository.save(question);

    }

    private void checkMemberRole(long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.get().getMemberId() == 1) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_REGISTRATION_QUESTION);
        }
    }

    public Question findVerifiedQuestion(long questionId) {
        Optional<Question> optionalQuestion =
                questionRepository.findById(questionId);
        Question findQuestion =
                optionalQuestion.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        return findQuestion;

    }

    private Question checkAuthority(Question question, Member member) {
        if (question.getQuestionStatus() == Question.QuestionStatus.QUESTION_DELETE) { // 게시글이 삭제된 상태인 경우 예외처리
            throw new BusinessLogicException(ExceptionCode.QUESTION_DELETED);
        } else if (question.getSecretStatus() == Question.SecretStatus.SECRET) { // 비밀글인 경우
            // 조회하는 회원이 해당 비밀 게시글의 작성자이거나 관리자인 경우
            if (question.getMember().getMemberId() == member.getMemberId() || question.getMember().getEmail().equals("admin@gmail.com")) {
                addViews(question); // 조회수 증가
                return question;
            }else{
                throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
            }
        }else{ // 공개글인 경우 그냥 조회수 증가
            addViews(question); // 조회수 증가
            return question;
        }
    }

    private void addViews(Question question) { // 조회수 증가 로직
        question.setViews(question.getViews() + 1);
        questionRepository.save(question);
    }


}
