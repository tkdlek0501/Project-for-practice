package com.productservice.demo.controller.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 회원 등록 폼

@Getter @Setter
@ToString
public class CreateMemberForm {
	
	// member
	@NotBlank(message = "아이디를 입력해주세요.")
	private String username;
	
	@Pattern(regexp="[a-zA-Z1-9]{6,12}", message = "비밀번호는 영어와 숫자로 포함해서 6~12자리 이내로 입력해주세요.")
	private String password;
	
	@NotBlank(message = "이름을 입력해주세요.")
	private String name;
	
	@NotNull(message = "나이를 입력해주세요.")
	@Min(value=1, message="나이는 최소 1세 이상으로 입력하세요.")
	private Integer age;
	
	@NotBlank(message = "등급을 선택해주세요.")
	private String grade; 
	// validation을 이용하기 위해 grade를 받을 때는 String 으로 받고 서버에서 Enum으로 처리해주기
	
	// address
	@NotBlank(message = "도시명을 입력해주세요.")
	private String city;
	
	@NotBlank(message = "도로명을 입력해주세요.")
	private String street;
	
	@NotBlank(message = "우편번호흫 입력해주세요.")
	@Size(min = 5, max = 5, message = "5자리를 입력하셔야 합니다.")
	private String zipcode;
	// String 으로 받고 int 로 컨버팅 할 것
	
}
