package com.example.QABulletinBoard.member.mapper;

import com.example.QABulletinBoard.member.Member;
import com.example.QABulletinBoard.member.MemberPostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    // MemberPostDto -> Member
    Member memberPostDtoToMember(MemberPostDto memberPostDto);
}
