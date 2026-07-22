package com.hotel.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;
	
	public MemberService(MemberRepository memberRepository) {
	this.memberRepository = memberRepository;
	}

	public Member insert(Member member) {
		return memberRepository.save(member);
	}
	

}
