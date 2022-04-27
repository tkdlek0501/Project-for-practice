package com.productservice.demo.service.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.productservice.demo.domain.Member;
import com.productservice.demo.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

	private final MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<Member> findMember = memberRepository.findOneByUsername(username);
		
		if(!findMember.isPresent()) {
			throw new UsernameNotFoundException("입력한 ID와 일치하는 유저가 조회되지 않습니다.");
		}
		
		Member member = findMember.get();
		
		return member;
	}
	
}
