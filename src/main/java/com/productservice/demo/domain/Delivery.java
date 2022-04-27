package com.productservice.demo.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {
	
	@Id @GeneratedValue
	@Column(name = "delivery_id")
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private DeliveryStatus status;
	
	private int zipcode;
	
	private String city;
	
	private String street;
	
	// 연관 관계 매핑
	@OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Order order;
	
	// 생성 메서드
	public static Delivery createDelivery(
			int zipcode,
			String city,
			String street
			) {
		Delivery delivery = new Delivery();
		delivery.setZipcode(zipcode);
		delivery.setCity(city);
		delivery.setStreet(street);
		delivery.setStatus(DeliveryStatus.READY);
		
		return delivery;
	}
	
	// 배송 완료
	public void complete() {
		this.setStatus(DeliveryStatus.COM);	
	}
}
