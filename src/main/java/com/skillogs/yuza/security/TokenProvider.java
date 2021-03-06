package com.skillogs.yuza.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.skillogs.yuza.domain.account.Account;
import com.skillogs.yuza.domain.account.Role;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
public class TokenProvider implements TokenAuthenticationService {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORISATION_HEADER = "Authorization";

    private final JWTVerifier verifier;
    private final KeyFactory keyFactory;

    @Autowired
    public TokenProvider(@Value("${key.rsa.private}") String keyPriv,
                         @Value("${key.rsa.public}") String keyPub) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        this.keyFactory = KeyFactory.getInstance("RSA");
        Algorithm a = Algorithm.RSA256(
                getPublicKey(keyPub),
                getPrivateKey(keyPriv));
        this.verifier = JWT.require(a).build();
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest req) {
        DecodedJWT token = isValid(req.getHeader(AUTHORISATION_HEADER));
        if (token== null) return null;
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(token.getClaim("role").as(SimpleGrantedAuthority.class));

        Account principal = new Account(token.getClaim("email").asString());
        principal.setId(token.getClaim("sub").asString());
        principal.setRole(Role.valueOf(token.getClaim("role").asString()));

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private DecodedJWT isValid(String token) {
        if (StringUtils.isEmpty(token)) return null;
        try {
            return verifier.verify(token.replace(TOKEN_PREFIX, ""));
        } catch (JWTVerificationException exception){
            return null;
        }
    }

    private RSAPublicKey getPublicKey(String filename) throws IOException, InvalidKeySpecException {

        byte[] encoded = Files.readAllBytes(Paths.get(filename));

        String publicKeyContent = new String(encoded);

        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        return (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
    }

    private RSAPrivateKey getPrivateKey(String filename) throws IOException, InvalidKeySpecException {
        byte[] encoded = Files.readAllBytes(Paths.get(filename));

        String privateKeyContent = new String(encoded);

        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "");

        byte[] data = Base64.getDecoder().decode(privateKeyContent);

        /* Add PKCS#8 formatting */
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(0));
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
        v2.add(DERNull.INSTANCE);
        v.add(new DERSequence(v2));
        v.add(new DEROctetString(data));
        ASN1Sequence seq = new DERSequence(v);
        byte[] privKey = seq.getEncoded("DER");

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privKey);
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }
}
