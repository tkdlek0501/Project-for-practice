package com.productservice.demo.controller.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 장바구니 등록 폼

@Getter
@Setter
@ToString
public class CreateCartForm {
	
	@NotNull(message = "옵션을 선택해주세요.")
	private Long optionId;
	
	@NumberFormat(pattern = "###,###")
	private int price;
	
	@Min(value = 1, message = "최소 수량은 1개 입니다. 1개 이상 입력해주세요.")
	private int count;
	
	// member
	private Long memberId;
	
	public CreateCartForm addMemberId(Long memberId) {
		this.memberId = memberId;
		return this;
	}
	
}
