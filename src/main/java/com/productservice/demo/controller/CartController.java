package com.productservice.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.productservice.demo.controller.form.CreateCartForm;
import com.productservice.demo.controller.form.UpdateCartForm;
import com.productservice.demo.domain.Cart;
import com.productservice.demo.domain.Member;
import com.productservice.demo.domain.Option;
import com.productservice.demo.service.CartService;
import com.productservice.demo.service.OptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CartController {
	
	private final CartService cartService;
	private final OptionService optionService;
	
	// 목록
	@GetMapping("/carts")
	public String carts(Authentication auth, Model model) {
		if(auth == null) { return "redirect:/doLogout"; }
		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		
		List<Cart> carts = cartService.myAll(memberId);
		model.addAttribute("carts", carts);
		
		return "cart/carts";
	}
	
	// 조회
	@GetMapping("/carts/{id}")
	public String cart(
			Authentication auth, 
			Model model,
			@PathVariable("id") Long id) {
		if(auth == null) { return "redirect:/doLogout"; }
		Member member = (Member) auth.getPrincipal();
		Long memberId = member.getId();
		
		Cart cart = cartService.myOne(id, memberId);
		UpdateCartForm form = UpdateCartForm.createForm(cart.getId(), cart.getOption().getId(), cart.getPrice(), cart.getCount());
		
		// 선택한 옵션
		Option myOption = optionService.findOne(cart.getOption().getId());
		// 전체 옵션
		List<Option> options = optionService.findAll();
		
		model.addAttribute("cartForm", form);
		model.addAttribute("myOption", myOption);
		model.addAttribute("options", options);
		
		return "cart/cart";
	}
	
	// 수정
	@PostMapping("/carts/{id}")
	public String updateCart(
			@Validated @ModelAttribute("cartForm") UpdateCartForm form,
			BindingResult bindingResult,
			@PathVariable("id") Long id,
			Model model,
			RedirectAttributes redirectAttributes
			) {
		
		log.info("수정할 폼 : {}", form);
		
		if(bindingResult.hasErrors()) {
			bindingResult.reject(null, "값에 오류가 있습니다.");
			return "cart/cart";
		}
		// 선택한 옵션
		Option myOption = optionService.findOne(form.getOptionId());
		List<Option> options = optionService.findAll();
		
		model.addAttribute("myOption", myOption);
		model.addAttribute("options", options);
		
		// 수정
		Map<String,Object> result = cartService.update(form);
		// 실패시
		if(result.get("error") != null) {
			
			if(result.get("error") == "NotEnoughStockException") {
				bindingResult.reject(null, "장바구니에 넣는 개수가 남아있는 재고보다 많습니다.");
			}else if(result.get("error") == "Exception") {
				bindingResult.reject(null, "장바구니 수정에 오류가 있습니다.");
			}
			
			return "cart/cart";
		}
		
		// 성공
		redirectAttributes.addAttribute("id", result.get("id"));
		return "redirect:/carts/{id}";
	}
	
}
