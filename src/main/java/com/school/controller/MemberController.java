package com.school.controller;

import com.school.dto.MemberDto;
import com.school.enums.MemberType;
import com.school.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing members (students and teachers).
 * <p>
 * Provides CRUD endpoints under {@code /members}.
 */
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * Constructs the controller with the required service.
     *
     * @param memberService the member service
     */
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Creates a new member.
     *
     * @param dto the member data
     * @return the created member with HTTP 201
     */
    @PostMapping
    public ResponseEntity<MemberDto> createMember(@Valid @RequestBody MemberDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(dto));
    }

    /**
     * Retrieves a member by its ID.
     *
     * @param id the member ID
     * @return the member data
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    /**
     * Retrieves all members of a given type.
     *
     * @param type the member type (STUDENT or TEACHER)
     * @return list of members matching the type
     */
    @GetMapping
    public ResponseEntity<List<MemberDto>> getMembersByType(@RequestParam MemberType type) {
        return ResponseEntity.ok(memberService.getMembersByType(type));
    }

    /**
     * Updates an existing member.
     *
     * @param id  the member ID
     * @param dto the updated member data
     * @return the updated member
     */
    @PutMapping("/{id}")
    public ResponseEntity<MemberDto> updateMember(@PathVariable Long id, @Valid @RequestBody MemberDto dto) {
        return ResponseEntity.ok(memberService.updateMember(id, dto));
    }

    /**
     * Deletes a member by its ID.
     *
     * @param id the member ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
