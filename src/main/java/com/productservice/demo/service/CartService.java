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
			Cart cart = Cart.createBuilder()
					.price(form.getPrice())
					.count(form.getCount())
					.member(member)
					.option(option)
					.build();
			
			// 이미 있는지 검사
			List<Cart> findCart = cartRepository.findAllByMemberAndOpt(member.getId(), option.getId());
			if(findCart.size() > 0) throw new IllegalStateException("이미 장바구니에 같은 옵션의 상품이 있습니다. 장바구니에서 수정해주세요.");
			
			cartRepository.save(cart);
			result.put("id", cart.getId());
		} catch (NotEnoughStockException e) {
			log.info("장바구니 등록 예외 발생 :", e.getMessage());
			result.put("error", "NotEnoughStockException");
		} catch (IllegalStateException e) {
			log.info("장바구니 등록 예외 발생 :", e.getMessage());
			result.put("error", "DuplicationException");
		} catch (Exception e) {
			log.info("장바구니 등록 예외 발생");
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
			Cart cart = Cart.modifyBuilder()
					.price(form.getPrice())
					.count(form.getCount())
					.option(option)
					.build();
			
			// 이미 있는지 검사 (옵션을 변경했을 때만)
			if(!findCart.getOption().getId().equals(option.getId())) {
				List<Cart> chkCart = cartRepository.findAllByMemberAndOpt(findCart.getMember().getId(), option.getId());
				if(chkCart.size() > 0) throw new IllegalStateException("이미 장바구니에 같은 옵션의 상품이 있습니다.");
			}
			
			findCart.modify(cart);
			result.put("id", findCart.getId());
		} catch (NotEnoughStockException e) {
			log.info("장바구니 수정 예외 발생 :", e.getMessage());
			result.put("error", "NotEnoughStockException");
		} catch (IllegalStateException e) {
			log.info("장바구니 등록 예외 발생 :", e.getMessage());
			result.put("error", "DuplicationException");
		} catch (Exception e) {
			log.info("장바구니 수정 예외 발생");
			result.put("error", "Exception");
		}
		
		return result;
	}
	
	// 삭제
	public void deleteCart(Long id) {
		Cart cart = cartRepository.findOne(id);
		
		cartRepository.deleteOne(cart);
	}
	
	
	
}
