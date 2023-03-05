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

    private void verifyExistsEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) { // 이미 존재하는 회원인 경우 예외 발생
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXIXTS);
        }
    }
}
