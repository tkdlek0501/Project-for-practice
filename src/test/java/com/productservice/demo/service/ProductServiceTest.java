package com.productservice.demo.service;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.aspectj.weaver.loadtime.Options;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.productservice.demo.controller.form.CreateOptionForm;
import com.productservice.demo.controller.form.CreateProductForm;
import com.productservice.demo.controller.form.UpdateOptionForm;
import com.productservice.demo.controller.form.UpdateProductForm;
import com.productservice.demo.domain.Category;
import com.productservice.demo.domain.Option;
import com.productservice.demo.domain.OrderStatus;
import com.productservice.demo.domain.Product;
import com.productservice.demo.domain.ProductOption;
import com.productservice.demo.repository.OptionRepository;
import com.productservice.demo.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class ProductServiceTest {
	
	@Autowired ProductService productService;
	@Autowired ProductRepository productRepository;
	@Autowired CategoryService categoryService;
	@Autowired OptionRepository optionRepository;
	@Autowired OptionService optionService;
	@Autowired EntityManager em;
	
	// 등록
	@Test
	public void create() throws Exception{
		// given
		// 카테고리 등록
		Category cat = Category.createCategory("카테고리1"); 
		Long catId = categoryService.create(cat);
		// 상품 등록
		CreateProductForm form = new CreateProductForm();
		form.setCategoryId(catId);
		
		String writerData = "str1,str2,str3,str4";
		MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", "name.png", writerData.getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> images = new ArrayList<>();
		images.add(mockImage);
		
		form.setImage(images);
		form.setName("상품1");
		form.setOptionItems("옵션명");
		
		List<CreateOptionForm> option = new ArrayList<>();
		CreateOptionForm optionForm = CreateOptionForm.createOptionForm("옵션1", 100);
		option.add(optionForm);
		
		form.setOption(option);
		form.setPrice(10000);
		form.setStatus("ORDER");
		
		// when
		Long id = productService.create(form);
		
		// then
		// 조회
		Product product = productRepository.findOne(id);
		assertEquals(product.getCategory().getId(), catId);
		
		Long poId = product.getProductOption().getId();
		List<Option> options = optionRepository.findAllByPoId(poId);
		assertEquals(options.get(0).getNames(), "옵션1");
	}
	
	// 수정
	@Test
	public void update() throws Exception{
		// given
		// 카테고리 등록
		Category cat = Category.createCategory("카테고리1"); 
		Long catId = categoryService.create(cat);
		// 상품 등록
		CreateProductForm form = new CreateProductForm();
		form.setCategoryId(catId);
		String writerData = "str1,str2,str3,str4";
		MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", "name.png", writerData.getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> images = new ArrayList<>();
		images.add(mockImage);
		form.setImage(images);
		form.setName("상품1");
		form.setOptionItems("옵션명");
		List<CreateOptionForm> option = new ArrayList<>();
		CreateOptionForm optionForm = CreateOptionForm.createOptionForm("옵션1", 100);
		option.add(optionForm);
		form.setOption(option);
		form.setPrice(10000);
		form.setStatus("ORDER");
		Long id = productService.create(form); // 등록
		
		// 조회
		Product product = productRepository.findOne(id);
		Long poId = product.getProductOption().getId();
		List<Option> options = optionRepository.findAllByPoId(poId); // 옵션
		
		// 수정 세팅
		UpdateOptionForm optionForm2 = UpdateOptionForm.createOptionForm(options.get(0).getId(), "옵션2", 200);
		List<UpdateOptionForm> optionForms = new ArrayList<>();
		optionForms.add(optionForm2);
		UpdateProductForm productForm = UpdateProductForm.createUpdateProductForm(id, "상품명2", 20000, "ORDER", null, "옵션명2", optionForms, catId);
		
		// when
		productService.modifyProduct(productForm);
		
		// then
		Product resultProduct = productRepository.findOne(id);
		assertEquals(resultProduct.getName(), "상품명2");
		assertEquals(resultProduct.getPrice(), 20000);
		
		Long resultPoId = resultProduct.getProductOption().getId();
		List<Option> resultOptions = optionRepository.findAllByPoId(resultPoId);
		assertEquals(resultOptions.get(0).getNames(), "옵션2");
	}
	
	// 옵션 삭제
	@Test
	public void deleteOption() throws Exception{
		// given
		// 카테고리 등록
		Category cat = Category.createCategory("카테고리1"); 
		Long catId = categoryService.create(cat);
		// 상품 등록
		CreateProductForm form = new CreateProductForm();
		form.setCategoryId(catId);
		String writerData = "str1,str2,str3,str4";
		MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", "name.png", writerData.getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> images = new ArrayList<>();
		images.add(mockImage);
		form.setImage(images);
		form.setName("상품1");
		form.setOptionItems("옵션명");
		List<CreateOptionForm> option = new ArrayList<>();
		CreateOptionForm optionForm = CreateOptionForm.createOptionForm("옵션1", 100);
		option.add(optionForm);
		form.setOption(option);
		form.setPrice(10000);
		form.setStatus("ORDER");
		Long id = productService.create(form); // 등록
		// 상품 조회 
		Product resultProduct = productService.findProduct(id);
		// 옵션 조회
		List<Option> options = optionRepository.findAllByPoId(resultProduct.getProductOption().getId());
		Long deleteId = options.get(0).getId();
		
		// when
		optionService.deleteOne(deleteId);
		
		// then
		assertEquals(null, optionRepository.findOne(deleteId));
	}
	
	
	// 삭제
	@Test
	public void delete() throws Exception{
		// given
		// 카테고리 등록
		Category cat = Category.createCategory("카테고리1"); 
		Long catId = categoryService.create(cat);
		// 상품 등록
		CreateProductForm form = new CreateProductForm();
		form.setCategoryId(catId);
		String writerData = "str1,str2,str3,str4";
		MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", "name.png", writerData.getBytes(StandardCharsets.UTF_8));
		List<MultipartFile> images = new ArrayList<>();
		images.add(mockImage);
		form.setImage(images);
		form.setName("상품1");
		form.setOptionItems("옵션명");
		List<CreateOptionForm> option = new ArrayList<>();
		CreateOptionForm optionForm = CreateOptionForm.createOptionForm("옵션1", 100);
		option.add(optionForm);
		form.setOption(option);
		form.setPrice(10000);
		form.setStatus("ORDER");
		Long id = productService.create(form); // 등록
		
		// when
		productService.deleteProduct(id);
		
		// then
		assertEquals(null, productService.findProduct(id));
	}
}
