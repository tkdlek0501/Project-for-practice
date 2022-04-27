package com.productservice.demo.repository.spec;

import java.util.List;

import com.productservice.demo.domain.Category;

public interface CategoryRepository {
	
	// 저장
	public void save(Category category); 
	
	// 조회
	public Category findOne(Long categoryId);
	
	// 목록
	public List<Category> findAll();
	
	// 검색
	public Category search();
	
	// 삭제
	public void deleteOne(Category findCat);
}
