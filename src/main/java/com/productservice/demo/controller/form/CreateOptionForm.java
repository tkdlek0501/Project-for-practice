package com.productservice.demo.controller.form;

import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@AllArgsConstructor -> 이거 쓰면 등록폼 템플릿에서 *{option[0].~} 할 때 오류 발생
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateOptionForm {
	
	@NotBlank(message = "해당 옵션 이름을 입력해주세요") 
	private String names;
	
	@NotBlank(message = "해당 옵션 재고를 입력해주세요")
	private Integer stockQuantity;
	
	
	public static CreateOptionForm createOptionForm(
			String names, 
			int stockQuantity
			) {
		CreateOptionForm form = new CreateOptionForm();
		form.setNames(names);
		form.setStockQuantity(stockQuantity);
		
		return form;
	}

}
