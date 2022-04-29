package com.productservice.demo.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.productservice.demo.controller.form.CreateCartForm;
import com.productservice.demo.controller.form.UpdateCartForm;
import com.productservice.demo.domain.Cart;
import com.productservice.demo.domain.Member;
import com.productservice.demo.domain.Option;
import com.productservice.demo.exception.NotEnoughStockException;
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
	public Map<String,Object> create(CreateCartForm form) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		
		Member member = memberRepository.findOne(form.getMemberId());
		Option option = optionRepository.findOne(form.getOptionId());
		
		try {
			Cart cart = Cart.createCart(form.getPrice(), form.getCount(), member, option);
			cartRepository.save(cart);
			
			result.put("id", cart.getId());
		} catch (NotEnoughStockException e) {
			log.info("장바구니 등록 예외 발생 :", e.getMessage());
			result.put("error", "NotEnoughStockException");
		} catch (Exception e) {
			log.info("주문 예외 발생");
			result.put("error", "Exception");
		}
		
		return result;
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
	public Map<String,Object> update(UpdateCartForm form) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		
		// 스냅샷
		Cart findCart = cartRepository.findOne(form.getId());
		
		// 수정할 option 조회
		Option option = optionRepository.findOne(form.getOptionId());
		
		// 수정할 내용으로 객체 생성
		try {
			Cart cart = Cart.updateCart(form.getPrice(), form.getCount(), option);
			findCart.modify(cart);
			result.put("id", findCart.getId());
		} catch (NotEnoughStockException e) {
			log.info("장바구니 등록 예외 발생 :", e.getMessage());
			result.put("error", "NotEnoughStockException");
		} catch (Exception e) {
			log.info("주문 예외 발생");
			result.put("error", "Exception");
		}
		
		return result;
	}
	
	
	
}
