package com.productservice.demo.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.productservice.demo.controller.form.CreateCartForm;
import com.productservice.demo.controller.form.UpdateCartForm;
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
		return cartRepository.findOne(cartId);
	}
	
	// 내 장바구니 목록
	public List<Cart> myAll(Long memberId) {
		return cartRepository.myAll(memberId);
	}
	
	// 상세 조회
	public Cart myOne(Long id, Long memberId) {
		return cartRepository.myOne(id, memberId).get();
	}
	
	// 수정
	public Long update(UpdateCartForm form) {
		
		// 스냅샷
		Cart findCart = cartRepository.findOne(form.getId());
		
		// 수정할 option 조회
		Option option = optionRepository.findOne(form.getOptionId());
		
		// 수정할 내용으로 객체 생성
		Cart cart = Cart.updateCart(form.getPrice(), form.getCount(), option);
		
		findCart.modify(cart);
		
		return findCart.getId();
	}
	
	
	
}
