package com.productservice.demo.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

import com.productservice.demo.controller.form.UpdateCategoryForm;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
	
	@Id @GeneratedValue
	@Column(name = "category_id")
	private Long id;
	
	private String name;
	
	// 연관 관계 매핑
	@OneToMany(mappedBy = "category")
	private List<Product> product = new ArrayList<>();
	
	// 생성 builder
	@Builder(builderClassName = "createBuilder", builderMethodName = "createBuilder")
	public Category(
			String name
			) {
		Assert.notNull(name, "카테고리 등록시 이름은 필수 입력 해야합니다.");
	}
	
	// 수정
	public Category modify(UpdateCategoryForm form) {
		
		if(form.getName() != null && !form.getName().isEmpty()) {
			this.setName(form.getName());
		}
		
		return this;
	}
	
	
}
