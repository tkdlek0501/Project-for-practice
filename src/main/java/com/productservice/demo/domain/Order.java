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
import javax.persistence.Table;

import org.springframework.format.annotation.NumberFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "orders") // order는 DB 예약어라 사용이 불가능!
@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Order {
	
	@Id @GeneratedValue
	@Column(name = "order_id")
	private Long id;
	
	@NumberFormat(pattern = "###,###")
	private int totalPrice;
	
	private LocalDateTime orderDate;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	// === 연관관계 매핑
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "delivery_id")
	private Delivery delivery;
	
	@OneToMany(mappedBy = "order")
	private List<OrderProduct> orderProduct = new ArrayList<>();
	
	// === 연관 관계 메서드
	
	public void setMember(Member member) {
		this.member = member;
		member.getOrder().add(this);
	}
	
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
		delivery.setOrder(this);
	}
	
	public void addOrderProduct(OrderProduct orderProduct) {
		this.orderProduct.add(orderProduct);
		orderProduct.setOrder(this);
	}
	
	// === 생성 메서드
	
	public static Order createOrder(
		Member member,
		Delivery delivery,
		int totalPrice
			) {
		Order order = new Order();
		order.setTotalPrice(totalPrice);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.ORDER);
		
		order.setMember(member);
		order.setDelivery(delivery);
		
		return order;
	}
	
	// TODO: exception catch 하기
	// 주문 취소
	public void cancel() {
		if(delivery.getStatus() == DeliveryStatus.COM) {
			throw new IllegalStateException("이미 배송 완료된 주문은 취소가 불가능 합니다.");
		}
		if(this.getStatus() == OrderStatus.CANCEL) {
			throw new IllegalStateException("이미 취소된 주문은 취소가 불가능 합니다.");
		}
		
		this.setStatus(OrderStatus.CANCEL);
		for(OrderProduct orderProduct : this.orderProduct) {
			orderProduct.cancel(); // 재고 감소
		}
	}
	
	// 배송 완료
	public void complete() {
		if(delivery.getStatus() == DeliveryStatus.COM) {
			throw new IllegalStateException("이미 배송 완료된 주문은 배송 완료가 불가능 합니다.");
		}
		if(this.getStatus() == OrderStatus.CANCEL) {
			throw new IllegalStateException("이미 취소된 주문은 배송 완료가 불가능 합니다.");
		}
		
		delivery.complete();
	}
	
}
