package com.productservice.demo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.productservice.demo.controller.form.UpdateMemberForm;
import com.productservice.demo.controller.form.UpdateMemberTestForm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements UserDetails{
	
	@Id @GeneratedValue
	@Column(name="member_id")
	private Long id;
	
	private String username;
	
	private String password;
	
	private String name;
	
	private int age;
	
	@Enumerated(EnumType.STRING)
	private Grade grade;
	
	private LocalDateTime registeredDate;
	
	// === 연관관계 매핑
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id")
	private Address address;
	
	@OneToMany(mappedBy = "member")
	private List<Order> order = new ArrayList<>();
	
	// === 연관 관계 메서드
	
	public void setAddress(Address address) {
		this.address = address;
		if(address != null) address.setMember(this); // null. 은 불가능!
	}
	
	public void addOrder(Order order) {
		this.order.add(order);
		order.setMember(this);
	}
	
	// === 생성 메서드
	public static Member createMember(
			String username, 
			String password, 
			String name, 
			int age,
			Grade grade,
			Address address
			) {
		Member member = new Member();
		member.setUsername(username);
		member.setPassword(password);
		member.setName(name);
		member.setAge(age);
		member.setGrade(grade);
		member.setRegisteredDate(LocalDateTime.now());
		member.setAddress(address);
		
		return member;
	}
	
	// 수정 메서드
	public static Member updateMember(
			Long memberId,
			String username,
			String password,
			String name,
			int age,
			Address address
			) {
		Member member = new Member();
		member.setId(memberId);
		member.setUsername(username);
		member.setPassword(password);
		member.setName(name);
		member.setAge(age);
		member.setAddress(address);
		
		return member;
	}
	
	// 수정
	public Member modify(UpdateMemberForm form) {
		if(form.getUsername() != null && !form.getUsername().isEmpty()) this.setUsername(form.getUsername());
		if(form.getPassword() != null && !form.getPassword().isEmpty()) this.setPassword(form.getPassword());
		if(form.getName() != null && !form.getName().isEmpty()) this.setName(form.getName());
		if(form.getAge() != 0) this.setAge(form.getAge());
		if(form.getGrade() != null) {
			Grade grade = Grade.valueOf(form.getGrade());
			this.setGrade(grade);
		}
		if(form.getCity() != null && !form.getCity().isEmpty()) this.getAddress().setCity(form.getCity());
		if(form.getStreet() != null && !form.getStreet().isEmpty()) this.getAddress().setStreet(form.getStreet());
		if(form.getZipcode() != null && !form.getZipcode().isEmpty()) this.getAddress().setZipcode(form.getZipcode());
		
		return this;
	}
	
	// 권한 
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		String grade = String.valueOf(this.grade);
		authorities.add(new SimpleGrantedAuthority("ROLE_" + grade)); // hasrole에서 ROLE_ 로 판단
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "Member [id=" + id + ", username=" + username + ", password=" + password + ", name=" + name + ", age="
				+ age + ", grade=" + grade + ", registeredDate=" + registeredDate + "]";
	}

	
}
