package com.productservice.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.productservice.demo.controller.form.UpdateCategoryForm;
import com.productservice.demo.domain.Category;
import com.productservice.demo.repository.spec.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
	
	private final CategoryRepository catRepository;
	
	// 카테고리 생성
	@Transactional
	public Long create(Category category) {
		
		catRepository.save(category);
		
		log.info("생성된 categoryId : {}", category.getId());
		
		return category.getId();
	}
	
	// 카테고리 목록
	public List<Category> findCategories() {
		return catRepository.findAll();
	}
	
	// 카테고리 조회
	public Category findCategory(Long categoryId) {
		return catRepository.findOne(categoryId);
	}
	
	// 카테고리 수정
	@Transactional
	public Long modifyCategory(UpdateCategoryForm form) {
		
		// 기존 카테고리
		Category findCat = catRepository.findOne(form.getId());
		
		findCat.modify(form);
		
		return findCat.getId();
	}
	
	// 카테고리 삭제
	@Transactional
	public void deleteCategory(Long categoryId) {
		
		Category findCat = catRepository.findOne(categoryId);
		
		catRepository.deleteOne(findCat);
	}
}
