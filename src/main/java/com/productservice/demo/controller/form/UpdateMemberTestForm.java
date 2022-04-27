package com.productservice.demo.controller.form;

import javax.validation.constraints.NotEmpty;

import com.productservice.demo.domain.Address;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// 회원 수정용

@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMemberTestForm {
	
	@NotEmpty(message = "회원 고유 식별자가 없습니다.")
	private Long id;
	
	private String username;
	
	private String password;
	
	private String name;
	
	private int age;
	
	private Address address;
	
	// 생성 메서드
	public static UpdateMemberTestForm createMemberForm(
			Long id,
			String username,
			String password,
			String name,
			Integer age,
			Address address
			) {
		UpdateMemberTestForm form = new UpdateMemberTestForm();
		form.setId(id);
		form.setUsername(username);
		form.setPassword(password);
		form.setName(name);
		if(age != 0) form.setAge(age);
		form.setAddress(address);
		
		return form;
	}
	
	
}
