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

// 장바구니

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
	
	@Id @GeneratedValue
	@Column(name = "cart_id")
	private Long id;
	
	private int price;
	
	private int count;
	
	private int totalPrice;
	
	// 연관 관계 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_id")
	private Option option;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	// 생성메서드
	public static Cart createCart(
			int price,
			int count,
			Member member,
			Option option
			) {
		Cart cart = new Cart();
		cart.setPrice(price);
		cart.setCount(count);
		cart.setTotalPrice(price * count);
		cart.setMember(member);
		cart.setOption(option);
		
		return cart;
	}
	
}
