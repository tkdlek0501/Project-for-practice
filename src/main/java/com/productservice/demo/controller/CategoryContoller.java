package com.productservice.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.productservice.demo.controller.form.CreateCategoryForm;
import com.productservice.demo.controller.form.UpdateCategoryForm;
import com.productservice.demo.domain.Category;
import com.productservice.demo.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
public class CategoryContoller {
	
	private final CategoryService categoryService;
	
	// 카테고리 목록
	@GetMapping("")
	public String categories(Model model) {
		
		List<Category> category = categoryService.findCategories();
		
		model.addAttribute("category", category);
		return "category/categories"; 
	}
	
	// 카테고리 등록 폼
	@GetMapping("/new")
	public String createCategoryForm(Model model) {
		model.addAttribute("form", new CreateCategoryForm());
		return "category/newCategory";
	}
	
	// 카테고리 등록
	@PostMapping("/new")
	public String createCategory(
			@Validated @ModelAttribute("form") CreateCategoryForm form,
			BindingResult bindingResult
			) {
		
		// 에러 처리
		if(bindingResult.hasErrors()) {
			log.info("카테고리 등록 잘못된 값 바인딩 error={}", bindingResult);
			return "category/newCategory";
		}
		
		// category 생성
		Category category = Category.createBuilder()
				.name(form.getName())
				.build();
		
		// 등록
		Long id = categoryService.create(category);
		
		// 등록 실패 시
		if(id == null) {
			log.info("카테고리 등록 실패");
			bindingResult.reject("duplicatonError", null, "이미 등록돼있는 이름입니다.");
			return "category/newCategory";
		}
		
		// 성공시
		log.info("카테고리 등록 {}", id);
		return "redirect:/admin/categories";
	}
	
	// 카테고리 조회(수정 폼)
	@GetMapping("/{categoryId}")
	public String category(Model model, @PathVariable("categoryId") Long categoryId) {
		
		Category category = categoryService.findCategory(categoryId);
		log.info("조회한 카테고리 : {}", category);
		UpdateCategoryForm getCategory = UpdateCategoryForm.createCategoryForm(category.getId(), category.getName());
		
		model.addAttribute("category", getCategory);
		return "category/category";
	}
	
	
	// 카테고리 수정
	@PostMapping("/{categoryId}")
	public String updateCategory(
			@ModelAttribute("category") UpdateCategoryForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes
			) {
		
		// 에러 처리
		if(bindingResult.hasErrors()) {
			return "category/category";
		}
		
		// 수정
		Long id = categoryService.modifyCategory(form);
		
		// 실패시
		if(id == null) {
			bindingResult.reject("duplicationError", null, "이미 등록돼있는 이름입니다.");
			return "category/category";
		}
		
		// 성공시
		redirectAttributes.addAttribute("categoryId", id);
		return "redirect:/admin/categories/{categoryId}";
	}
	
	
	// 카테고리 삭제
	@GetMapping("/{id}/remove")
	public String deleteCategory(@PathVariable("id") Long categoryId) {
		
		categoryService.deleteCategory(categoryId);
		return "redirect:/admin/categories";
	}
	
	
}
