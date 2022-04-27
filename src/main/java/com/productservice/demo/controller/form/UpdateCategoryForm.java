package com.productservice.demo.controller.form;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCategoryForm {
	
	private Long id;
	
	private String name;
	
	public static UpdateCategoryForm createCategoryForm(
			Long id,
			String name
			) {
		UpdateCategoryForm form = new UpdateCategoryForm();
		form.setId(id);
		form.setName(name);
		
		return form;
	}
	
}
