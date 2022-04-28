package com.productservice.demo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.productservice.demo.domain.Option;
import com.productservice.demo.domain.Product;
import com.productservice.demo.domain.ProductStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductRepository {
	
	private final EntityManager em;
	
	// 상품 저장
	public void save(Product product) {
		em.persist(product);
	}
	
	// 상품 조회
	public Product findOne(Long productId) {
		log.info("조회하려는 product 식별자 : {}", productId);
		return em.find(Product.class, productId);
	}
	
	// 상품 목록
	public List<Product> findAll() {
		return em.createQuery("select p from Product p", Product.class)
				.getResultList();
	}
	
	// 상품 삭제
	public void deleteOne(Product product) {
		em.remove(product);
	}
	
	// 노출 상태 상품 목록
	public List<Product> findShowAll() {
		return em.createQuery("select p from Product p where p.status = :status", Product.class)
				.setParameter("status", ProductStatus.SHOW)
				.getResultList();
	}
	
}
