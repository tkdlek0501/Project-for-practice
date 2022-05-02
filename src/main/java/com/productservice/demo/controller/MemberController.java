package com.productservice.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.productservice.demo.controller.form.CreateMemberForm;
import com.productservice.demo.controller.form.UpdateMemberForm;
import com.productservice.demo.domain.Address;
import com.productservice.demo.domain.Grade;
import com.productservice.demo.domain.Member;
import com.productservice.demo.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
@Slf4j
public class MemberController {
	
	private final MemberService memberService;
	
	// 회원 목록
	@GetMapping("")
	public String members(Model model) {
		List<Member> members = memberService.findMembers();
		
		model.addAttribute("members", members);
		return "member/members";
	}
	
	// 회원 등록 폼
	@GetMapping("/new")
	public String createMemberForm(Model model) {
		model.addAttribute("form", new CreateMemberForm());
		return "member/newMember";
	}
	
	// 회원 등록  ※ModelAttribute 쓸 때 set까지 자동으로 같이 해주는 것이므로 setter 가 필요하다
	@PostMapping("")
	public String createMember(
			@Validated @ModelAttribute("form") CreateMemberForm form, 
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes
			) {
		
		// 에러 처리
		if(bindingResult.hasErrors()) {
			log.info("회원 등록 잘못된 값 바인딩 error={}", bindingResult);
			return "member/newMember";
		}
		
		// converting
		Grade grade = Grade.valueOf(form.getGrade());
		
		Address address = Address.createAddress(form.getCity(), form.getStreet(), form.getZipcode());
		// Member member = Member.createMember(form.getUsername(), form.getPassword(), form.getName(), form.getAge(), grade, address);
		Member member = Member.createBuilder() 
						.username(form.getUsername())
						.password(form.getPassword())
						.name(form.getName())
						.age(form.getAge())
						.grade(grade)
						.address(address)
						.build();
		
		Long id = memberService.join(member);
		
		// 회원 가입 실패시
		if(id == null) {
			log.info("회원 가입 실패");
			bindingResult.reject("duplicationError", null, "이미 사용하고 있는 아이디 입니다. 다른 아이디를 사용해주세요.");
			return "member/newMember";
		}
		
		// 성공시
		// redirect 설정
		redirectAttributes.addAttribute("memberId", id);
		//redirectAttributes.addAttribute("status", true); ?뒤 쿼리스트링
		return "redirect:/admin/members/{memberId}"; // 성공시 회원 목록으로
	}
	
	// 회원 조회 (수정 폼)
	@GetMapping("/{memberId}")
	public String member(Model model, @PathVariable("memberId") Long memberId) {
		
		Member member = memberService.findMember(memberId);
		log.info("조회한 회원 : {}", member);
		UpdateMemberForm getMember = UpdateMemberForm.showUpdateMemberForm(member.getId(), member.getUsername(), member.getPassword(), member.getName(), member.getAge(), member.getGrade(), member.getAddress(), member.getRegisteredDate());
		
		model.addAttribute("member", getMember);
		return "member/member";
	}
	
	// 회원 수정
	@PostMapping("/{memberId}")
	public String updateMember(
			@Validated @ModelAttribute("member") UpdateMemberForm form, 
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes
			) {
		
		log.info("회원 수정 중입니다. form : {}", form);
		
		// 에러 처리
		if(bindingResult.hasErrors()) {
			log.info("회원 수정 잘못된 값 바인딩 error={}", bindingResult);
			return "member/member";
		}
		
		// 회원 수정
		Long id = memberService.modifyMember(form);
		
		// 회원 수정 실패시
		if(id == null) {
			log.info("회원 수정 실패");
			bindingResult.reject("modifyMemberFail", null, "이미 사용하고 있는 아이디 입니다. 다른 아이디를 사용해주세요.");
			return "member/member";
		}
		
		// 성공시
		// redirect 설정
		redirectAttributes.addAttribute("memberId", id);
		return "redirect:/admin/members/{memberId}"; // 성공시 상세 화면
	}
	
	// 회원 삭제
	@GetMapping("/{id}/remove")
	public String deleteMember(@PathVariable("id") Long memberId) {
		
		log.info("회원을 삭제합니다. 식별 번호 : {}", memberId);
		
		memberService.deleteMember(memberId);
		return "redirect:/admin/members";
	}
	
	// 아이디 찾기
	
	// 비밀번호 찾기
}
