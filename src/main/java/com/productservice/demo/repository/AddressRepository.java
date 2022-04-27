package com.productservice.demo.repository;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.productservice.demo.domain.Address;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AddressRepository {
	
	private final EntityManager em;
	
	// 저장
	public void save(Address address) {
		em.persist(address);
	}
	
	// 조회
	public Address findOne(Long id) {
		return em.find(Address.class, id);
	}
	
	// 삭제
	public void deleteOne(Address address) {
		em.remove(address);
	}
}
