package com.productservice.demo.controller.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateOrderForm {
	
	// delivery
	@NotNull(message="우편번호를 입력해주세요.")
	private Integer zipcode;
	@NotEmpty(message="지역명을 입력해주세요.")
	private String city;
	@NotEmpty(message="도로명을 입력해주세요.")
	private String street;
	
	// options
	@NotNull(message="옵션을 선택해주세요.")
	private Long optionId;
	
	// orderProduct
	@NotNull(message="주문 가격에 이상이 있습니다.")
	@NumberFormat(pattern = "###,###")
	private Integer price;
	@NotNull(message="주문 개수를 입력해주세요.")
	@Min(value = 1, message="주문 개수는 1개 이상이어야 합니다.")
	private Integer count;
	
	// member
	private Long memberId; // auth로 받아올 것
	
	// memberId 추가 메서드
	public CreateOrderForm addMemberId(
			Long memberId
			) {
		this.memberId = memberId;
		return this;
	}
	
	
}
