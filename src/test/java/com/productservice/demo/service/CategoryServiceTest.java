package com.productservice.demo.service;



import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.productservice.demo.controller.form.UpdateCategoryForm;
import com.productservice.demo.domain.Category;
import com.productservice.demo.repository.spec.CategoryRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CategoryServiceTest {
	
	@Autowired CategoryService categoryService;
	@Autowired CategoryRepository categoryRepository;
	
	// 카테고리 등록
	@Test
	public void create() throws Exception{
		// given
		Category cat = Category.createBuilder()
				.name("카테고리1")
				.build();
		
		// when
		Long savedId = categoryService.create(cat);
		
		// then
		assertEquals(cat, categoryRepository.findOne(savedId));
	}
	
	// 카테고리 수정
	@Test
	public void update() throws Exception{
		// given
		// 생성
		Category cat = Category.createBuilder()
				.name("카테고리1")
				.build();
		Long categoryId = categoryService.create(cat);
		
		// 수정할 값
		UpdateCategoryForm form = UpdateCategoryForm.createCategoryForm(categoryId, "카테고리 수정"); 
		
		// when
		// 수정
		categoryService.modifyCategory(form);
			
		// then
		// 수정된 결과
		Category resultCat = categoryRepository.findOne(categoryId);
		assertEquals(resultCat.getName(), "카테고리 수정");
	} 
	
	
	// 카테고리 삭제
	@Test
	public void delete() throws Exception{
		// given
		// 등록
		Category cat = Category.createBuilder()
				.name("카테고리1")
				.build();
		Long categoryId = categoryService.create(cat);
		
		// when
		// 삭제
		categoryService.deleteCategory(categoryId);
		
		// then
		Category resultCat = categoryRepository.findOne(categoryId);
		assertEquals(resultCat, null);
	}
	
}
