package com.productservice.demo.service;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.productservice.demo.controller.form.UpdateMemberForm;
import com.productservice.demo.controller.form.UpdateMemberTestForm;
import com.productservice.demo.domain.Address;
import com.productservice.demo.domain.Grade;
import com.productservice.demo.domain.Member;
import com.productservice.demo.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class MemberServiceTest {
	
	@Autowired MemberService memberService;
	@Autowired MemberRepository memberRepository;
	@Autowired EntityManager em;
	
	Address address = Address.createBuilder()
			.city("경기")
			.street("남로")
			.zipcode("12345")
			.build();
	
	Address address2 = Address.createBuilder()
			.city("경기")
			.street("남로")
			.zipcode("67890")
			.build();
	
	// 회원 가입
	@Test
	public void join() throws Exception{
		// given
		//Member member = Member.createMember("HJ", "김현준", "1234", 29, Grade.ADMIN, address);
		Member member = Member.createBuilder() 
				.username("HJ")
				.password("1234")
				.name("김현준")
				.age(29)
				.grade(Grade.ADMIN)
				.address(address)
				.build();
		
		// when
		Long savedId = memberService.join(member);
		
		// then
		// em.flush(); // 쿼리 동작 강제
		assertEquals(member, memberRepository.findOne(savedId));
	}
	
	
	// 중복 검증 -> try-catch 로 바꿔서 exception 안터짐
	@Test // (expected = IllegalStateException.class)
	public void validation() throws Exception {
		// given
		Member member1 = Member.createBuilder() 
				.username("HJ")
				.password("1234")
				.name("김현준")
				.age(29)
				.grade(Grade.ADMIN)
				.address(address)
				.build();
		
		Member member2 = Member.createBuilder() 
				.username("HJ")
				.password("1234")
				.name("김현준")
				.age(29)
				.grade(Grade.ADMIN)
				.address(address2)
				.build();
		
		// when
		memberService.join(member1);
		//memberService.join(member2);
		
		// then
		// fail("중복 검증 테스트 실패(예외가 발생해야 합니다.)");
		assertEquals(null, memberService.join(member2));
	}
	
	// 회원 수정
	@Test
	//@Rollback(false)
	public void updateMember() throws Exception{
		// given
			// 등록 
			Member member = Member.createBuilder() 
					.username("HJ")
					.password("1234")
					.name("김현준")
					.age(29)
					.grade(Grade.ADMIN)
					.address(address)
					.build();
			
			Long memberId = memberService.join(member);
			
			// 등록한  member 찾아오기 (영속성 컨텍스트 등록)
			Member findMember = memberRepository.findOne(memberId);
			
			// 수정할 form
			UpdateMemberForm form = UpdateMemberForm.createMemberForm(memberId, "HJ2", "1234", "김현준2", 27, address2);
			
		// when
			// 수정
			findMember.modify(form);
		
		// then
			Member resultMember = memberRepository.findOne(memberId);
			assertEquals(address2.getCity(), resultMember.getAddress().getCity());
			assertEquals("HJ2", resultMember.getUsername());
			assertEquals("김현준2", resultMember.getName());
			assertEquals(27, resultMember.getAge());
	}
	
	// 회원 삭제
	@Test
	public void deleteMember() throws Exception{
		//given
			// 등록
			Member member = Member.createBuilder() 
					.username("HJ")
					.password("1234")
					.name("김현준")
					.age(29)
					.grade(Grade.ADMIN)
					.address(address)
					.build();
			
			Long memberId = memberService.join(member);
			
			// 등록한 member 찾아오기
			Member findMember = memberRepository.findOne(memberId);

		//when
			// 삭제
			memberRepository.deleteOne(findMember);
						
		//then
			Member resultMember = memberRepository.findOne(memberId); // 조회가 되면 안됨
			assertEquals(resultMember, null); 
	}
}
