package com.productservice.demo.service;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.productservice.demo.controller.form.CreateOrderForm;
import com.productservice.demo.domain.Address;
import com.productservice.demo.domain.Delivery;
import com.productservice.demo.domain.Grade;
import com.productservice.demo.domain.Member;
import com.productservice.demo.domain.Option;
import com.productservice.demo.domain.Order;
import com.productservice.demo.domain.OrderProduct;
import com.productservice.demo.domain.OrderStatus;
import com.productservice.demo.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class OrderServiceTest {
	
	@Autowired OrderService orderService;
	@Autowired OrderRepository orderRepository;
	@Autowired EntityManager em;
	
	// 등록
	@Test
	public void create() throws Exception{
		// given
		Address address = createAddress();
		Member member = createMember(address);
		Option option = createOption();
				
		CreateOrderForm form = new CreateOrderForm();
		form.setCity("city");
		form.setCount(10);
		form.setMemberId(member.getId());
		form.setOptionId(option.getId());
		form.setPrice(10000);
		form.setStreet("street");
		form.setZipcode(12345);
		
		// when
		Map<String,Object> result = orderService.order(form);
		
		// then
		Long id = (Long) result.get("id");
		Order order = orderService.findOne(id);
		assertEquals(order.getMember(), member);
		assertEquals(order.getDelivery().getCity(), "city");
		//assertNotEquals(order, null);
	}
	
	private Address createAddress() {
		Address address = Address.createAddress("city", "street", "zipcode");
		em.persist(address);
		return address;
	}
	
	private Member createMember(Address address) {
		Member member = Member.createMember("test", "123123", "tester", 12, Grade.USER, address);
		em.persist(member);
		return member;
	}
	
	private Option createOption() {
		Option option = Option.createOption("옵션1", 10);
		em.persist(option);
		return option;
	}
	
	// 주문 취소
	@Test
	public void cancel() throws Exception{
		// given
		Address address = createAddress();
		Member member = createMember(address);
		Option option = createOption();
				
		CreateOrderForm form = new CreateOrderForm();
		form.setCity("city");
		form.setCount(10);
		form.setMemberId(member.getId());
		form.setOptionId(option.getId());
		form.setPrice(10000);
		form.setStreet("street");
		form.setZipcode(12345);
		
		Map<String,Object> result = orderService.order(form);
		
		// when
		orderService.cancelOrder((Long) result.get("id"));
		
		// then
		Order order = orderService.findOne((Long) result.get("id"));
		assertEquals(order.getStatus(), OrderStatus.CANCEL);
	}
	
	// 배송 완료; 주문 취소 후에는 되면 안됨
	@Test(expected = IllegalStateException.class)
	public void complete() throws Exception{
		// given
		Address address = createAddress();
		Member member = createMember(address);
		Option option = createOption();
				
		CreateOrderForm form = new CreateOrderForm();
		form.setCity("city");
		form.setCount(10);
		form.setMemberId(member.getId());
		form.setOptionId(option.getId());
		form.setPrice(10000);
		form.setStreet("street");
		form.setZipcode(12345);
		
		Map<String,Object> result = orderService.order(form);
		Long orderId = (Long) result.get("id");
		orderService.cancelOrder(orderId); // 주문 취소
		
		// when
		orderService.completeDelivery(orderId); // 배송 완료
		
		// then
		fail("TEST 실패! 주문 취소 후에는 예외가 발생해야 합니다.");
	}
	
}
