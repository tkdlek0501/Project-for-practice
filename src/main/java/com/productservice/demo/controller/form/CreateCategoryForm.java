package com.productservice.demo.controller.form;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 카테고리 등록 폼 DTO

@Getter
@Setter
@ToString
public class CreateCategoryForm {
	
	@NotBlank(message = "이름을 입력해주세요.")
	private String name;
	
}
