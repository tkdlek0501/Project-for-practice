package com.productservice.demo.controller.form;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.productservice.demo.domain.Address;
import com.productservice.demo.domain.Grade;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 회원 수정 폼

@Getter @Setter
@ToString
public class UpdateMemberForm {
	
	// member
	@NotNull
	private Long id;
	
	@NotBlank(message = "아이디를 입력해주세요.")
	private String username;
	
	@Pattern(regexp="[a-zA-Z1-9]{6,12}", message = "비밀번호는 영어와 숫자로 포함해서 6~12자리 이내로 입력해주세요.")
	private String password;
	
	@NotBlank(message = "이름을 입력해주세요.")
	private String name;
	
	@NotNull(message = "나이를 입력해주세요.")
	@Min(value=0, message="나이는 최소 1세 이상으로 입력하세요.")
	private Integer age;
	
	@NotBlank(message = "등급을 선택해주세요.")
	private String grade; 
	
	// address
	@NotBlank(message = "도시명을 입력해주세요.")
	private String city;
	
	@NotBlank(message = "도로명을 입력해주세요.")
	private String street;
	
	@NotBlank(message = "우편번호흫 입력해주세요.")
	@Size(min = 5, max = 5, message = "5자리를 입력하셔야 합니다.")
	private String zipcode;
	
	private LocalDateTime registeredDate;
	
	// 생성 메서드
	public static UpdateMemberForm createMemberForm(
			Long id,
			String username,
			String password,
			String name,
			Integer age,
			Address address
			) {
		UpdateMemberForm form = new UpdateMemberForm();
		form.setId(id);
		form.setUsername(username);
		form.setPassword(password);
		form.setName(name);
		if(age != 0) form.setAge(age);
		form.setCity(address.getCity());
		form.setStreet(address.getStreet());
		form.setZipcode(address.getZipcode());
		
		return form;
	}
	
	// form 조회 메서드
	public static UpdateMemberForm showUpdateMemberForm(
			Long id,
			String username,
			String password,
			String name,
			Integer age,
			Grade grade,
			Address address,
			LocalDateTime registeredDate
			) {
		
		// converting
		String stringGrade = String.valueOf(grade);
		
		UpdateMemberForm form = new UpdateMemberForm();
		form.setId(id);
		form.setUsername(username);
		form.setPassword(password);
		form.setName(name);
		form.setAge(age);
		form.setGrade(stringGrade);
		form.setCity(address.getCity());
		form.setStreet(address.getStreet());
		form.setZipcode(address.getZipcode());
		form.setRegisteredDate(registeredDate);
		
		return form;
	}
}
