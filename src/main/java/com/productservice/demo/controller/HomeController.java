package com.productservice.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.productservice.demo.controller.form.CreateMemberForm;
import com.productservice.demo.domain.Address;
import com.productservice.demo.domain.Grade;
import com.productservice.demo.domain.Member;
import com.productservice.demo.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final MemberService memberService;
	
	// index
	@GetMapping("")
	public String home(Authentication auth) {
		
		String grade = null;
		if(auth != null) { 
			grade = getGrade(auth);
			if(grade.equals("ADMIN")) {
				return "redirect:/admin";
			} 
		}
		
		return "redirect:/user";
	}

	private String getGrade(Authentication auth) {
		Member member = (Member) auth.getPrincipal();
		Grade grd = member.getGrade();
		String grade = String.valueOf(grd);
		return grade;
	}
	
	// admin index 페이지
	@GetMapping("/admin")
	public String adminHome() {
		return "home";
	}
	
	// user index 페이지
	@GetMapping("/user")
	public String userHome() {
		return "home";
	}
	
	// login 페이지
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	// logiin 실패 페이지 - test
	@GetMapping("/fail")
	public String fail() {
		return "login";
	}
	
	// 회원가입 페이지
	@GetMapping("/login/join")
	public String join(Model model) {
		model.addAttribute("form", new CreateMemberForm());
		return "join";
	}
	
	// 회원 가입
	@PostMapping("/login/join")
	public String createMember(
			@Validated @ModelAttribute("form") CreateMemberForm form, 
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes
			) {
		
		// 에러 처리
		if(bindingResult.hasErrors()) {
			log.info("회원 등록 잘못된 값 바인딩 error={}", bindingResult);
			return "join";
		}
		
		// converting
		Grade grade = Grade.valueOf(form.getGrade());
		
		Address address = Address.createAddress(form.getCity(), form.getStreet(), form.getZipcode());
		Member member = Member.createMember(form.getUsername(), form.getPassword(), form.getName(), form.getAge(), grade, address);
		Long id = memberService.join(member);
		
		// 회원 가입 실패시
		if(id == null) {
			log.info("회원 가입 실패");
			bindingResult.reject("duplicationError", null, "이미 사용하고 있는 아이디 입니다. 다른 아이디를 사용해주세요.");
			return "join";
		}
		
		// 성공시
		return "redirect:/login"; // 로그인 페이지
	}
}
