package com.productservice.demo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.productservice.demo.controller.form.CreateOrderForm;
import com.productservice.demo.controller.form.CreateOrdersForm;
import com.productservice.demo.domain.Cart;
import com.productservice.demo.domain.Delivery;
import com.productservice.demo.domain.Member;
import com.productservice.demo.domain.Option;
import com.productservice.demo.domain.Order;
import com.productservice.demo.domain.OrderProduct;
import com.productservice.demo.dto.OrderSearch;
import com.productservice.demo.exception.NotEnoughStockException;
import com.productservice.demo.repository.CartRepository;
import com.productservice.demo.repository.MemberRepository;
import com.productservice.demo.repository.OptionRepository;
import com.productservice.demo.repository.OrderProductRepository;
import com.productservice.demo.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final OptionRepository optionRepository;
	private final OrderProductRepository orderProductRepository;
	private final CartRepository cartRepository;
	
	// 주문 등록
	@Transactional
	public Map<String,Object> order(CreateOrderForm form) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		
		// 엔티티 조회
		Member member = memberRepository.findOne(form.getMemberId());
		Option option = optionRepository.findOne(form.getOptionId());
		
		// delivery 생성 - order 와 동시에 생성
		//Delivery delivery = Delivery.createDelivery(form.getZipcode(), form.getCity(), form.getStreet());
		Delivery delivery = Delivery.createBuilder()
				.zipcode(form.getZipcode())
				.city(form.getCity())
				.street(form.getStreet())
				.build();
		
		try {
			// order 생성
			int totalPrice = form.getPrice() * form.getCount(); // 해당 주문 총 가격
			Order order = Order.createOrder(member, delivery, totalPrice);

			// orderProduct 생성 ; 이때 재고 감소
			OrderProduct orderProduct = OrderProduct.createOrderProduct(option, form.getPrice(), form.getCount(), order);
			
			orderRepository.save(order);
			orderProductRepository.save(orderProduct);
			
			log.info("order 성공 : {}", order.getId());
			result.put("id", order.getId());
			return result;
		} catch (NotEnoughStockException e) {
			log.info("주문 예외 발생 : 재고 부족");
			result.put("error", "NotEnoughStock");
			return result;
		} catch (Exception e) {
			log.info("주문 예외 발생");
			result.put("error", "CreateError");
			return result;
		} 
	}
	
	// 여러개 주문
	@Transactional
	public Map<String, Object> multiOrder(CreateOrdersForm form) {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		
		Member member = memberRepository.findOne(form.getMemberId());
		Delivery delivery = Delivery.createBuilder()
				.zipcode(form.getZipcode())
				.city(form.getCity())
				.street(form.getStreet())
				.build();
		
		int totalPrice = 0;
		for(int i = 0; i < form.getIds().size(); i++) {
			totalPrice += form.getPrices().get(i) * form.getCounts().get(i); // 해당 주문 총 가격
		}	
		
		try {
			// order 생성
			Order order = Order.createOrder(member, delivery, totalPrice);
			orderRepository.save(order);
			
			// orderProduct 생성 ; 이때 재고 감소
			for(int i = 0; i < form.getIds().size(); i++) {
				Cart cart = cartRepository.findOne(form.getIds().get(i));
				Option option = optionRepository.findOne(cart.getOption().getId());
				OrderProduct orderProduct = OrderProduct.createOrderProduct(option, form.getPrices().get(i), form.getCounts().get(i), order);
				orderProductRepository.save(orderProduct);
			}
			
			log.info("order 성공");
			result.put("id", order.getId());
		} catch (NotEnoughStockException e) {
			log.info("주문 예외 발생 : 재고 부족");
			result.put("error", "NotEnoughStock");
		} catch (Exception e) {
			log.info("주문 예외 발생");
			result.put("error", "CreateError");
		} 
		
		return result;
	}
	
	// 나의 주문 목록
	public List<Order> myAll(Long memberId) {
		return orderRepository.myAll(memberId);
	}
	
	// 모든 주문 목록
	public List<Order> findAll() {
		return orderRepository.findAll();
	}
	
	// 검색 포함 주문 목록
	public List<Order> searchAll(OrderSearch orderSearch) {
		return orderRepository.searchAll(orderSearch);
	}
	
	// 주문 취소 
	@Transactional
	public void cancelOrder(Long orderId) {
		// 엔티티 조회
		Order order = orderRepository.findOne(orderId);
		
		order.cancel();
	}
	
	// 배송 완료
	@Transactional
	public void completeDelivery(Long orderId) {
		Order order = orderRepository.findOne(orderId);
		
		order.complete();
	}
	
	// 주문 조회
	public Order findOne(Long orderId) {
		return orderRepository.findOne(orderId);
	}
	
	// 나의 주문 조회
	public Order myOne(Long orderId, Long memberId) {
		return orderRepository.myOne(orderId, memberId).get();
	}
	
}
