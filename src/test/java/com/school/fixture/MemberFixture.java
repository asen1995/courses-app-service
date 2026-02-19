package com.school.fixture;

import com.school.dto.MemberDto;
import com.school.entity.Member;
import com.school.enums.MemberType;

import java.util.Set;

public class MemberFixture {

    public static MemberDto memberDto(String memberName, int memberAge,
            String memberGroup, MemberType memberType,
            Set<Long> courseIds) {
        return MemberDto.builder()
                .name(memberName)
                .age(memberAge)
                .group(memberGroup)
                .type(memberType)
                .courseIds(courseIds)
                .build();
    }

    public static MemberDto memberDto(Long id, String memberName,
            MemberType memberType, Set<Long> courseIds) {
        return MemberDto.builder()
                .id(id)
                .name(memberName)
                .type(memberType)
                .courseIds(courseIds)
                .build();
    }

    public static Member memberEntity(Long id) {
        var member = new Member();
        member.setId(id);
        return member;
    }
}
