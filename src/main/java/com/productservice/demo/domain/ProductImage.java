package com.productservice.demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {
	
	@Id @GeneratedValue
	@Column(name = "product_image_id")
	private Long id;
	
	private String originalName;
	
	private String storeName;
	
	// 연관 관계 매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;
	
	// 생성 메서드
	public static ProductImage createProductImage(
			String originalName,
			String storeName
			) {
		ProductImage productImage = new ProductImage();
		productImage.setOriginalName(originalName);
		productImage.setStoreName(storeName);
		
		return productImage;
	}
	
	// product의 cascade가 아닌 추가 생성시
	public static ProductImage addProductImage(
			String originalName,
			String storeName,
			Product product
			) {
		ProductImage productImage = new ProductImage();
		productImage.setOriginalName(originalName);
		productImage.setStoreName(storeName);
		productImage.setProduct(product);
		
		return productImage;
	}
}
