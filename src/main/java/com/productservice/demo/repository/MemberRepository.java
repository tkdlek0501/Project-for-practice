package com.productservice.demo.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.productservice.demo.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {
	
	private final EntityManager em;
	
	// 회원 저장
	public void save(Member member) {
		em.persist(member);
	}
	
	// 회원 조회
	public Member findOne(Long memberId) {
		return em.find(Member.class, memberId);
//		 List<Member> member = em.createQuery("select m from Member m left join m.address a where m.id = :id", Member.class)
//			 .setParameter("id", memberId)
//			 .getResultList();
//		 
//		 return member.stream().findAny();
	}
	
	// 회원 목록
	public List<Member> findAll(){
		return em.createQuery("select m from Member m left join m.address a", Member.class)
				.getResultList();
	}
	
	// TODO: 수정은 transaction 변경 감지로 쿼리 작동
	
	// 이름으로 회원 조회
	public List<Member> findByName(String name){
		return em.createQuery("select m form Member m where m.name = :name", Member.class)
				.setParameter("name", name)
				.getResultList();
	}
	
	// 아이디로 회원 조회 // getSingleResult 사용시 null 체크 해줘야 한다. -> getResultList 로 변경 + optional(NPE를 피하기 위한 방법) 로 해결 
	public Optional<Member> findOneByUsername(String username) {
		log.info("아이디로 회원 조회");
		List<Member> member = em.createQuery("select m from Member m where m.username = :username", Member.class)
				.setParameter("username", username)
				.getResultList(); 
		
		if(member.size() != 0) log.info("아이디로 찾아온 member : {}", member.get(0));
		
		return member.stream().findAny();
	}
	
	// 회원 삭제
	public void deleteOne(Member member) {
		em.remove(member);
	}
}
