package com.productservice.demo.service;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.productservice.demo.controller.form.CreateCartForm;
import com.productservice.demo.controller.form.CreateOptionForm;
import com.productservice.demo.controller.form.CreateProductForm;
import com.productservice.demo.controller.form.UpdateCartForm;
import com.productservice.demo.domain.Address;
import com.productservice.demo.domain.Cart;
import com.productservice.demo.domain.Category;
import com.productservice.demo.domain.Grade;
import com.productservice.demo.domain.Member;
import com.productservice.demo.domain.Option;
import com.productservice.demo.domain.Product;
import com.productservice.demo.repository.CartRepository;
import com.productservice.demo.repository.OptionRepository;
import com.productservice.demo.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class CartServiceTest {
	
	@Autowired ProductService productService;
	@Autowired ProductRepository productRepository;
	@Autowired OptionRepository optionRepository;
	@Autowired CategoryService categoryService;
	@Autowired CartService cartService;
	@Autowired CartRepository cartRepository;
	@Autowired MemberService memberService;
	
	// 장바구니 등록
	@Test
	public void create() throws Exception{
		// given
		// 카테고리 등록
		Category cat = Category.createBuilder()
				.name("카테고리1")
				.build();
		
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
		
		Long id = productService.create(form); // 옵션 포함 상품 저장
		Product product = productRepository.findOne(id);
		
		Long poId = product.getProductOption().getId();
		List<Option> options = optionRepository.findAllByPoId(poId);
		log.info("optionId : {}", options.get(0).getId());
		
		// 멤버 등록
		Address address = Address.createAddress("경기", "남로", "12345");
		Member member = Member.createBuilder() 
				.username("HJ")
				.password("1234")
				.name("김현준")
				.age(29)
				.grade(Grade.ADMIN)
				.address(address)
				.build();
		
		Long memberId = memberService.join(member);
		
		// cart
		CreateCartForm cartForm = new CreateCartForm();
		cartForm.setMemberId(memberId);
		cartForm.setOptionId(options.get(0).getId());
		cartForm.setCount(1);
		cartForm.setPrice(10000);
		
		// when
		Map<String,Object> result = cartService.create(cartForm); 
		Long cartId = (Long) result.get("id");
		log.info("cartId : {}", cartId);
		
		// then
		Cart cart = cartService.findOne(cartId);
		assertEquals(1, cart.getCount());
		assertEquals(10000, cart.getPrice());
		assertEquals(10000, cart.getTotalPrice());
		assertEquals(options.get(0).getId(), cart.getOption().getId());
	}
	
	// 장바구니 수정
	@Test
	public void update() throws Exception{
		// given
			// 카테고리 등록
			Category cat = Category.createBuilder()
				.name("카테고리1")
				.build();
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
			
			Long id = productService.create(form);
			Product product = productRepository.findOne(id);
			
			Long poId = product.getProductOption().getId();
			List<Option> options = optionRepository.findAllByPoId(poId);
			log.info("optionId : {}", options.get(0).getId());
			
			// 멤버 등록
			Address address = Address.createAddress("경기", "남로", "12345");
			Member member = Member.createBuilder() 
					.username("HJ")
					.password("1234")
					.name("김현준")
					.age(29)
					.grade(Grade.ADMIN)
					.address(address)
					.build();
			
			Long memberId = memberService.join(member);
			
			// cart
			CreateCartForm cartForm = new CreateCartForm();
			cartForm.setMemberId(memberId);
			cartForm.setOptionId(options.get(0).getId());
			cartForm.setCount(1);
			cartForm.setPrice(10000);
			
			Map<String,Object> result = cartService.create(cartForm); // 장바구니 등록
			Long cartId = (Long) result.get("id");
			
			// 수정할 값 세팅
			UpdateCartForm updateForm = new UpdateCartForm();
			updateForm.setId(cartId);
			updateForm.setPrice(20000);
			updateForm.setCount(2);
			updateForm.setOptionId(options.get(0).getId());
			
		// when
			Map<String,Object> updateResult = cartService.update(updateForm);
			Long updateId = (Long) updateResult.get("id");
			
		// then
			Cart updatedCart = cartService.findOne(updateId);
			assertEquals(20000, updatedCart.getPrice());
			assertEquals(2, updatedCart.getCount());
			assertEquals(40000, updatedCart.getTotalPrice());
	}
	
	
}
