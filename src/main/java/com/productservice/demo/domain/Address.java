package com.productservice.demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.productservice.demo.controller.form.UpdateMemberTestForm;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
	
	@Id @GeneratedValue
	@Column(name = "address_id")
	private Long id;
	
	private String city;
	
	private String street;
	
	private String zipcode;
	
	// === 연관관계 매핑
	
	@OneToOne(mappedBy="address", fetch = FetchType.LAZY)
	private Member member;
	
	// === 생성 builder
	@Builder(builderClassName = "createBuilder", builderMethodName = "createBuilder")
	public Address(
			String city,
			String street,
			String zipcode
			) {
		this.city = city;
		this.street = street;
		this.zipcode = zipcode;
	}
	
	// === 생성 메서드
//	public static Address createAddress(
//			String city,
//			String street,
//			String zipcode
//			) {
//		Address address = new Address();
//		address.setCity(city);
//		address.setStreet(street);
//		address.setZipcode(zipcode);
//		
//		return address;
//	}

//	public Address modify(UpdateMemberTestForm form) {
//		Address address = new Address();
//		address.setCity(form.getAddress().getCity());
//		address.setStreet(form.getAddress().getStreet());
//		address.setZipcode(form.getAddress().getZipcode());
//		
//		return address;
//	}

}
