package com.hotel.util;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {
    private final SecretKey secretKey;
    private final int validSeconds;
    private final JwtParser jwtParser;
    
    public JwtUtils(String secretKeyStr, int validSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        // 這裡寫得很好！預先建立好 parser 物件效能最佳
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
        this.validSeconds = validSeconds;
    }

    public String generateToken(UserDetails user) {
        // 計算過期時間
        long expirationMillis = Instant.now()
                .plusSeconds(validSeconds)
                .toEpochMilli(); // 提示：直接用 toEpochMilli() 更簡潔安全

        // 準備 payload 內容
        Claims claims = Jwts.claims()
                .subject(user.getUsername()) // 🔥 修正：使用標準的 subject(sub) 欄位，這樣 getSubject() 才拿得到
                .issuedAt(new Date())
                .expiration(new Date(expirationMillis))
                .add("authorities", user.getAuthorities()) // 保留您自訂的權限清單
                .build();

        // 簽名後產生 JWT
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        // 🔥 修正：改用新版的 parseSignedClaims() 與 getPayload()
        Claims claims = jwtParser
                .parseSignedClaims(token) // 代替 parseClaimsJws
                .getPayload();            // 代替 getBody
        return claims.getSubject();
    }
    
    // 順便幫您補上驗證 Token 是否有效的方法，過濾器（Filter）會用到
    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Token 過期、被變造或格式不對會拋出異常
        }
    }
}
