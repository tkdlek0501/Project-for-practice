package com.productservice.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.productservice.demo.controller.form.UpdateMemberForm;
import com.productservice.demo.controller.form.UpdateMemberTestForm;
import com.productservice.demo.domain.Member;
import com.productservice.demo.repository.AddressRepository;
import com.productservice.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true) 
@RequiredArgsConstructor 
@Slf4j
public class MemberService {
	
	private final MemberRepository memberRepository;
	private final AddressRepository addressRepository;
	private final PasswordEncoder passwordEncoder;
	
	// 회원 가입(생성)
	@Transactional
	public Long join(Member member) {
		
		// 중복 검증
		try {
			validateDuplicateMember(member.getUsername());
		} catch (IllegalStateException e) {
			return null;
		}
		
		// 비밀번호 암호화
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		
		memberRepository.save(member);
		addressRepository.save(member.getAddress());
		
		return member.getId();
	}
	
	// 중복 검증 (아이디 중복 막음)
	private void validateDuplicateMember(String username) {
		
		log.info("중복 검증 실행 username : {}", username);
		
		Optional<Member> findMember = memberRepository.findOneByUsername(username);
		
		if(findMember.isPresent()) { // 값이 존재 하는지 boolean
			throw new IllegalStateException("이미 사용하고 있는 아이디입니다.");
		}
	}
	
	// 회원 목록
	public List<Member> findMembers(){
		return memberRepository.findAll();
	}
	
	// 회원 조회
	public Member findMember(Long memberId) {
		return memberRepository.findOne(memberId);
	}
	
	// 회원 수정
	@Transactional
	public Long modifyMember(UpdateMemberForm form) {
		
		// 받아온 username 있으면 
		if(form.getUsername() != null && !form.getUsername().isEmpty()) {
			// member 찾기
			Member member = memberRepository.findOne(form.getId());
			
			// 자신의 기존 아이디와 다르면 검증
			if(!member.getUsername().equals(form.getUsername())) {
				log.info("기존 아이디와 달라 검증 중입니다.");
				try {
					validateDuplicateMember(form.getUsername());
				} catch (IllegalStateException e) {
					return null;
				}
			}
		}	
		
		// 기존 멤버 (이 객체의 필드가 변경되면 감지한다)
		Member findMember = memberRepository.findOne(form.getId()); // 영속성 컨텍스트 등록
		
		// 수정 (변경 감지)
		findMember.modify(form);
		
		return findMember.getId();
	}
	
	// 회원 삭제
	@Transactional
	public void deleteMember(Long memberId) {
		
		Member findMember = memberRepository.findOne(memberId);
		
		memberRepository.deleteOne(findMember);
	}
}
