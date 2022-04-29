package com.productservice.demo.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.productservice.demo.domain.Order;
import com.productservice.demo.dto.OrderSearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderRepository {
	
	private final EntityManager em;
	
	// 주문 저장
	public void save(Order order) {
		em.persist(order);
	}
	
	// 주문 조회
	public Order findOne(Long orderId) {
		return em.find(Order.class, orderId);
	}
	
	// 나의 주문 조회
	public Optional<Order> myOne(Long orderId, Long memberId) {
		List<Order> order =  em.createQuery("select o from Order o where o.member.id = :memberId and o.id = :orderId", Order.class)
				.setParameter("memberId", memberId)
				.setParameter("orderId", orderId)
				.getResultList();
		
		return order.stream().findAny();
	}
	
	// 주문 목록 (전체)
	public List<Order> findAll(){
		return em.createQuery("select o from Order o", Order.class)
				.getResultList();
	}
	
	// 내 주문 목록
	public List<Order> myAll(Long memberId) {
		return em.createQuery("select o from Order o where o.member.id = :memberId", Order.class)
				.setParameter("memberId", memberId)
				.getResultList();
	}
	
	// 검색 포함 주문 목록
	public List<Order> searchAll(OrderSearch orderSearch) {
		
		String jpql = "select o from Order o join o.member m";
		boolean isFirstCondition = true;
		
		// 회원 이름 검색
		if(StringUtils.hasText(orderSearch.getMemberName())) {
			if(isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and"; 
			}
			jpql += " m.name like :name";
		}
		
		TypedQuery<Order> query = em.createQuery(jpql, Order.class)
				.setFirstResult(0) // offset
				.setMaxResults(1000); // limit
		
		if(StringUtils.hasText(orderSearch.getMemberName())) {
			query = query.setParameter("name", orderSearch.getMemberName());
		}
		
		return query.getResultList();
	}
	
}
