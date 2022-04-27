package com.productservice.demo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.NumberFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Product {
	
	@Id @GeneratedValue
	@Column(name = "product_id")
	private Long id;
	
	private String name;
	
	@NumberFormat(pattern = "###,###")
	private int price;
	
	@Enumerated(EnumType.STRING)
	private ProductStatus status;
	
	private LocalDateTime registeredDate;
	
	// 연관 관계 매핑
	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL) // productOption은 product와 등록 삭제 등 라이프 사이클 동일
	private ProductOption productOption;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE) // product 삭제시에는 하위 모두 삭제 되게
	private List<ProductImage> productImage = new ArrayList<>();
	
	// === 연관 관계 메서드
	
	public void setProductOption(ProductOption productOption) {
		this.productOption = productOption;
		productOption.setProduct(this);
	}
	
	public void setCategory(Category category) {
		this.category = category;
		category.getProduct().add(this);
	}
	
	public void addProductImage(ProductImage productImage) {
		this.productImage.add(productImage);
		productImage.setProduct(this);
	}
	
	// === 생성 메서드
	
	public static Product createProduct(
			String name,
			int price,
			ProductOption productOption,
			Category category
			) {
		Product product = new Product();
		product.setName(name);
		product.setPrice(price);
		product.setStatus(ProductStatus.SHOW);
		product.setRegisteredDate(LocalDateTime.now());
		
		product.setProductOption(productOption);
		product.setCategory(category);
		
		return product;
	}
	
	public static Product updateProduct(
			String name, 
			Integer price, 
			String status, 
			ProductOption productOption,
			Category category) {
		Product product = new Product();
		product.setName(name);
		product.setPrice(price);
		ProductStatus ps = ProductStatus.valueOf(status);
		product.setStatus(ps);
		product.setRegisteredDate(LocalDateTime.now());
		
		product.setProductOption(productOption);
		product.setCategory(category);
		
		return product;
	}
	
	// 수정
	public void modify(Product product) {
		// 상품
		if(product.getName() != null && !product.getName().isEmpty()) this.setName(product.getName());
		this.setPrice(product.getPrice());
		if(product.getStatus() != null) this.setStatus(product.getStatus());
		
		// 상품 옵션
		productOption.setOptionItems(product.getProductOption().getOptionItems());
		
		// 카테고리 - 조회해서 가져온 엔티티 넣어줌
		this.setCategory(product.getCategory());
	}

	
}
