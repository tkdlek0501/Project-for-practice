package com.productservice.demo.service;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
		
		Long id = productService.create(form);
		Product product = productRepository.findOne(id);
		
		Long poId = product.getProductOption().getId();
		List<Option> options = optionRepository.findAllByPoId(poId);
		log.info("optionId : {}", options.get(0).getId());
		
		// 멤버 등록
		Address address = Address.createAddress("경기", "남로", "12345");
		Member member = Member.createMember("HJ", "김현준", "1234", 29, Grade.ADMIN, address);
		Long memberId = memberService.join(member);
		
		// cart
		CreateCartForm cartForm = new CreateCartForm();
		cartForm.setMemberId(memberId);
		cartForm.setOptionId(options.get(0).getId());
		cartForm.setCount(1);
		cartForm.setPrice(10000);
		
		// when
		Long cartId = cartService.create(cartForm); 
		log.info("cartId : {}", cartId);
		
		// then
		Cart cart = cartService.findOne(cartId);
		assertEquals(1, cart.getCount());
		assertEquals(10000, cart.getPrice());
		assertEquals(10000, cart.getTotalPrice());
		assertEquals(options.get(0).getId(), cart.getOption().getId());
	}
	
}