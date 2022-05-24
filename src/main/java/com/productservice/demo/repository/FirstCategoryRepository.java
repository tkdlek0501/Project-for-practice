package com.productservice.demo.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.productservice.demo.domain.Category;
import com.productservice.demo.repository.spec.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FirstCategoryRepository implements CategoryRepository {

	private final EntityManager em;
	
	// 카테고리 저장
	@Override
	public void save(Category category) {
		em.persist(category);
	}

	// 카테고리 조회
	@Override
	public Category findOne(Long categoryId) {
		return em.find(Category.class, categoryId);
	}
	
	// 카테고리 전체 목록 -> 추후에 검색에 포함 예정
	@Override
	public List<Category> findAll() {
		return em.createQuery("select c from Category c", Category.class)
				.getResultList();
	}
	
	// 카테고리 검색
	@Override
	public Category search() {
		// TODO category 검색 메서드 구현
		return null;
	}
	
	// 카테고리 삭제
	@Override
	public void deleteOne(Category findCat) {
		em.remove(findCat);
	}
	
	// TODO: jpql left join 사용 예시
	// + DTO 로 꺼내오는 방법까지
//	em.createQuery("SELECT new com.example.demo.dto.BoardListResponse(b.id, b.boardTitle, b.boardContent, b.createDate, b.updateDate, b.deleteDate, u.nickname, u.accountType, COUNT(l.id))"
//			+ " FROM Board b"
//			+ " LEFT JOIN b.user u"
//			+ " LEFT JOIN b.likes l"
//			+ " GROUP BY b.id", 
//			BoardListResponse.class)
//			.getResultList();
	
}
