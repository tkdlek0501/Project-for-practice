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

import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	
	// === 생성 builder
	@Builder(builderClassName = "createBuilder", builderMethodName = "createBuilder")
	public Delivery(
			int zipcode,
			String city,
			String street
			) {
		Assert.notNull(zipcode, "zipcode must not be null");
		Assert.notNull(city, "city must not be null");
		Assert.notNull(street, "street must not be null");
		
		this.zipcode = zipcode;
		this.city = city;
		this.street = street;
		this.status = DeliveryStatus.READY;
	}
	
	// 배송 완료
	public void complete() {
		this.status = DeliveryStatus.COM;	
	}
}
