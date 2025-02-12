package iuh.fit.se.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import iuh.fit.se.entity.User;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.signerKey}")
    private String secretKey;

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())  // ✅ Dùng subject() thay vì setSubject()
                .claim("name", user.getFirstName())
                .issuedAt(new Date())      // ✅ Dùng issuedAt() thay vì setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 giờ
                .signWith(getSignKey())    // ✅ Không cần chỉ định thuật toán, tự động chọn HMAC-SHA
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()   // ✅ Sử dụng parserBuilder() thay vì parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
