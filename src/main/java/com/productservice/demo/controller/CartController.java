package com.productservice.demo.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.productservice.demo.domain.Cart;
import com.productservice.demo.domain.Member;
import com.productservice.demo.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CartController {
	
	private final CartService cartService;
	
	@GetMapping("/carts")
	public String carts(Authentication auth, Model model) {
		if(auth == null) { return "redirect:/doLogout"; }
		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		
		List<Cart> carts = cartService.myAll(memberId);
		model.addAttribute("carts", carts);
		
		return "cart/carts";
		
	}
	
}
