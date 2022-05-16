package com.productservice.demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.NumberFormat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 장바구니

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
	
	@Id @GeneratedValue
	@Column(name = "cart_id")
	private Long id;
	
	@NumberFormat(pattern = "###,###")
	private int price;
	
	private int count;
	
	@NumberFormat(pattern = "###,###")
	private int totalPrice;
	
	// 연관 관계 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_id")
	private Option option;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	// === 생성 builder
	@Builder(builderClassName = "createBuilder", builderMethodName = "createBuilder")
	public Cart(
			int price,
			int count,
			Member member,
			Option option
			) {
		this.price = price;
		this.count = count;
		this.totalPrice = (price*count);
		this.member = member;
		this.option = option;
		
		option.checkStock(count);
	}
	
	@Builder(builderClassName = "modifyBuilder", builderMethodName = "modifyBuilder")
	public Cart(
			int price,
			int count,
			Option option
			) {
		this.price = price;
		this.count = count;
		this.totalPrice = (price * count);
		this.option = option;
	}
	
	
	// 수정
	public void modify(Cart cart) {
		this.price = cart.getPrice();
		this.count = cart.getCount();
		this.totalPrice = cart.getTotalPrice();
		this.option = cart.getOption();
	}

	
}
