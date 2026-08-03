package com.hotel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Member;
import com.hotel.repository.MemberRepository;

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
