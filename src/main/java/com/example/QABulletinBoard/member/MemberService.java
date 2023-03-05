package com.example.QABulletinBoard.member;

import com.example.QABulletinBoard.exception.BusinessLogicException;
import com.example.QABulletinBoard.exception.ExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
//@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /*
     <회원 등록>
     1. 중복 이메일 검증
     */
    public Member createMember(Member member) {
        verifyExistsEmail(member.getEmail()); // 중복 이메일 검증
        return memberRepository.save(member);

    }

    private void verifyExistsEmail(String email) { // 중복 회원 검증
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) { // 이미 존재하는 회원인 경우 예외 발생
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    // 회원 조회 검증(해당 id의 회원이 존재하는지 확인)
    public Member findVerifiedMember(long memberId) {
        Optional<Member> optionalMember =
                memberRepository.findById(memberId);
        Member findMember =
                optionalMember.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }

//    public void checkMemberRole(String email) { // 회원 종류 검증
//        Optional<Member> member = memberRepository.findByEmail(email);
//        if (member.get().getEmail().equals("admin@gmail.com")) { // 관리자 계정은 질문 등록 불가
//            throw new BusinessLogicException(ExceptionCode.CANNOT_REGISTRATION_QUESTION);
//        }
//    }
}
