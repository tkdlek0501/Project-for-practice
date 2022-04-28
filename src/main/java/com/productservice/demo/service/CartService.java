package com.productservice.demo.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.productservice.demo.controller.form.CreateCartForm;
import com.productservice.demo.domain.Cart;
import com.productservice.demo.domain.Member;
import com.productservice.demo.domain.Option;
import com.productservice.demo.repository.CartRepository;
import com.productservice.demo.repository.MemberRepository;
import com.productservice.demo.repository.OptionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartService {
	
	private final CartRepository cartRepository;
	private final MemberRepository memberRepository;
	private final OptionRepository optionRepository;
	
	// 등록
	public Long create(CreateCartForm form) {
		
		Member member = memberRepository.findOne(form.getMemberId());
		Option option = optionRepository.findOne(form.getOptionId());
		
		Cart cart = Cart.createCart(form.getPrice(), form.getCount(), member, option);
		cartRepository.save(cart);
		
		return cart.getId();
	}
	
	// 조회
	public Cart findOne(Long cartId) {
		return cartRepository.findOne(cartId).get();
	}
	
	// 내 장바구니 목록
	public List<Cart> myAll(Long memberId) {
		return cartRepository.myAll(memberId);
	}
	
	
	
}
