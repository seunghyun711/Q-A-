package com.example.QABulletinBoard.member;

import com.example.QABulletinBoard.member.mapper.MemberMapper;
import com.example.QABulletinBoard.utils.UriCreator;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Validated
@RequestMapping(value = "/QA/members")
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/QA/members";
    private final MemberService memberService;
    private final MemberMapper mapper;

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }
    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberPostDto memberPostDto) {
        Member member = mapper.memberPostDtoToMember(memberPostDto);
        memberService.createMember(member);
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());

        return ResponseEntity.created(location).build();
    }
}
