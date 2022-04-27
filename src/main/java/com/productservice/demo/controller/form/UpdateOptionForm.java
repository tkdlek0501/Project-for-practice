package com.productservice.demo.controller.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateOptionForm {
	
	@NotNull
	private Long id;
	
	@NotBlank(message = "해당 옵션 이름을 입력해주세요") 
	private String names;
	
	@NotBlank(message = "해당 옵션 재고를 입력해주세요")
	private Integer stockQuantity;
	
	
	public static UpdateOptionForm createOptionForm(
			Long id,
			String names, 
			int stockQuantity
			) {
		UpdateOptionForm form = new UpdateOptionForm();
		form.setId(id);
		form.setNames(names);
		form.setStockQuantity(stockQuantity);
		
		return form;
	}
	
}
