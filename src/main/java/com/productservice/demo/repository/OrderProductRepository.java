package com.productservice.demo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.productservice.demo.domain.Order;
import com.productservice.demo.domain.OrderProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderProductRepository {
	
	private final EntityManager em;
	
	// 저장
	public void save (OrderProduct orderProduct) {
		em.persist(orderProduct);
	}
	
	// 목록
	public List<OrderProduct> findAll(Long id) {
		return em.createQuery("select op from OrderProduct op join fetch op.option o where op.order.id = :id", OrderProduct.class)
				.setParameter("id", id)
				.getResultList();
	} 
	
}
