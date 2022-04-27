package com.productservice.demo.controller.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 상품 등록 폼

@Getter 
@Setter
@ToString
public class CreateProductForm {
	
	// 상품
	@NotBlank(message = "상품 이름을 입력해주세요.")
	private String name;
	
	@NotNull(message = "가격을 입력해주세요.")
	private Integer price;
	
	@NotBlank(message = "노출 여부를 입력해주세요.")
	private String status;
	
	@NotNull(message = "이미지를 하나 이상 등록해주세요.")
	private List<MultipartFile> image;
	
	// productOption
	@NotBlank(message = "옵션 항목 이름을 입력해주세요.")
	private String optionItems; // 옵션 항목 이름
	
	// option
	private List<CreateOptionForm> option = new ArrayList<>();
	
	// category TODO: 상품 등록시 category 연결
	@NotNull(message = "카테고리를 선택해주세요.")
	private Long categoryId;
}
