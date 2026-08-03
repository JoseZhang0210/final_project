package com.hotel.service;

import com.hotel.entity.Token;
import com.hotel.repository.TokenRepository;

public class TokenService {
	private final TokenRepository tokenRepository;
	
	
	
	public TokenService(TokenRepository tokenRepository) {
		super();
		this.tokenRepository = tokenRepository;
	}



	public Token insert(Token token) {
		return tokenRepository.save(token);
	}
}
