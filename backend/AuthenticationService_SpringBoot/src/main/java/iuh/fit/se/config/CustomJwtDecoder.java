package iuh.fit.se.config;

import com.nimbusds.jose.JOSEException;
import iuh.fit.se.dto.request.IntrospectRequest;
import iuh.fit.se.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@Primary
public class CustomJwtDecoder  implements JwtDecoder {

    @Value("${jwt.signerKey}")
    String signingKey;

    @Autowired
    AuthenticationService authenticationService;

    NimbusJwtDecoder jwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(IntrospectRequest.builder()
                            .token(token)
                    .build());
            if(!response.isValid()) {
                throw new JwtException("Invalid JWT token");
            }
        } catch (JOSEException | ParseException e) {
            throw new JwtException("Invalid JWT token", e);
        }
        if(Objects.isNull(jwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(),"HS512");
            jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return jwtDecoder.decode(token);
    }
}