package com.productservice.demo.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.productservice.demo.domain.Cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CartRepository {
	
	private final EntityManager em;
	
	// 등록
	public void save(Cart cart) {
		em.persist(cart);
	}
	
	// 조회
	public Cart findOne(Long cartId) {
		return em.find(Cart.class, cartId);
//		List<Cart> cart = em.createQuery("select c from Cart c join fetch c.option where :cartId", Cart.class)
//									.setParameter("cartId", cartId)
//									.getResultList();
//		return cart.stream().findAny();
	}
	
	// 목록
	public List<Cart> myAll(Long memberId) {
		return em.createQuery("select c from Cart c where c.member.id = :memberId", Cart.class)
				.setParameter("memberId", memberId)
				.getResultList();
	}

	public Optional<Cart> myOne(Long id, Long memberId) {
		List<Cart> cart = em.createQuery("select c from Cart c where c.member.id = :memberId and c.id = :id", Cart.class)
				.setParameter("memberId", memberId)
				.setParameter("id", id)
				.getResultList();
		
		return cart.stream().findAny();
	}
	
}
