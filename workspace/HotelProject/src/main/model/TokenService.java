package com.hotel.model;

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
