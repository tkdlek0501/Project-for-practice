package com.productservice.demo.controller.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 여러개 주문 폼

@Getter
@Setter
@ToString
public class CreateOrdersForm {
	
	private List<Long> ids; // cartId
	
	private List<Integer> counts;
	
	private List<Integer> prices;
	
	@NotNull(message="우편번호를 입력해주세요.")
	private int zipcode;
	
	@NotEmpty(message="지역명을 입력해주세요.")
	private String city;
	
	@NotEmpty(message="도로명을 입력해주세요.")
	private String street;
	
	private Long memberId;
	
	public void addMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
}
