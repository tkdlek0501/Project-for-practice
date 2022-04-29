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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.productservice.demo.exception.NotEnoughStockException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "options")
@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {
	
	@Id @GeneratedValue
	@Column(name = "option_id")
	private Long id;
	
	private String names;
	
	private int stockQuantity;
	
	// 연관 관계 매핑
	@OneToMany(mappedBy = "option")
	private List<OrderProduct> orderProduct = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_option_id")
	private ProductOption productOption;
	
	@OneToMany(mappedBy = "option")
	private List<Cart> cart = new ArrayList<>();
	
	// === 생성 메서드
	public static Option createOption(
			String names,
			int stockQuantity
			) {
		Option option = new Option();
		option.setNames(names);
		option.setStockQuantity(stockQuantity);
		
		return option;
	}
	
	public static Option addOption(
			String names, 
			int stockQuantity, 
			ProductOption productOption
			) {
		Option option = new Option();
		option.setNames(names);
		option.setStockQuantity(stockQuantity);
		option.setProductOption(productOption);
		return option;
	}
	
	// 장바구니 추가시 재고 체크 (실제 개수 차감은 주문시)
	public void checkStock(int count) {
		int restStock = this.stockQuantity - count;
		
		if(restStock < 0) {
			throw new NotEnoughStockException("장바구니에 넣는 개수가 남아있는 재고보다 많습니다.");
		}
	}
	
	// 재고 감소
	public void removeStock(int count) {
		int restStock = this.stockQuantity - count;
		
		if(restStock < 0) {
			throw new NotEnoughStockException("재고가 충분하지 않습니다.");
		}
		
		this.stockQuantity = restStock;
	}
	
	// 재고 증가
	public void addStock(int count) {
		this.stockQuantity += count;
	}

	// 변경
	public void modify(Option option) {
		this.setNames(option.getNames());
		this.setStockQuantity(option.getStockQuantity());
	}
	
	
	
	
}
