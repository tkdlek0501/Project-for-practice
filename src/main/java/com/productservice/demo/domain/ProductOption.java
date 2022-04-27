package com.productservice.demo.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
	
	@Id @GeneratedValue
	@Column(name = "product_option_id")
	private Long id;
	
	private String optionItems; // 옵션 항목 이름
	
	// 연관 관계 매핑
	
	@OneToMany(mappedBy = "productOption", cascade = CascadeType.REMOVE) // productOption 삭제시 option 같이 삭제
	private List<Option> option = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;
	
	// === 연관 관계 메서드
	
	public void addOption(Option option) {
		this.option.add(option);
		option.setProductOption(this); // option에는 set 메서드 따로 안만들고 setter 이용했음
	}
	
	// === 생성 메서드
	
	public static ProductOption createProductOption(
			String optionItems
//			List<Option> options
			) {
		ProductOption productOption = new ProductOption();
		productOption.setOptionItems(optionItems);
		
//		if(options != null) {
//			for(Option option : options) {
//				productOption.addOption(option);
//			}
//		}
		
		return productOption;
	}
	
}
