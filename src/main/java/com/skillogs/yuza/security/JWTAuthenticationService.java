package com.skillogs.yuza.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class JWTAuthenticationService implements TokenAuthenticationService{
    private final JWTVerifier verifier;
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Autowired
    public JWTAuthenticationService(@Value("${key.rsa.private}") String keyPriv,
                                      @Value("${key.rsa.public}") String keyPub)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        Algorithm a = Algorithm.RSA256(
                getPublicKey(keyPub),
                getPrivateKey(keyPriv));
        verifier = JWT.require(a).build();
    }


    @Override
    public Authentication getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(HEADER_STRING);
        if (token == null) return null;
        if (!isValid(token)) return null;

        return Optional.ofNullable(getUsernameFromToken(token))
                .map(email -> new UsernamePasswordAuthenticationToken(email, null, emptyList()))
                .orElse(null);
    }

    private String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token.replace(TOKEN_PREFIX, ""));
            return jwt.getClaim("email").asString();
        } catch (JWTDecodeException exception){
            return null;
        }
    }

    private boolean isValid(String token) {
        try {
            verifier.verify(token.replace(TOKEN_PREFIX, ""));
            return true;
        } catch (JWTVerificationException exception){
            return false;
        }
    }
    private static RSAPublicKey getPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource file = new ClassPathResource(filename);
        String publicKeyContent = read(file.getInputStream());

        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");;

        KeyFactory kf = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        return (RSAPublicKey) kf.generatePublic(keySpecX509);
    }
    private static RSAPrivateKey getPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource file = new ClassPathResource(filename);

        String privateKeyContent = read(file.getInputStream());

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

        PKCS8EncodedKeySpec spec = new  PKCS8EncodedKeySpec(privKey);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) fact.generatePrivate(spec);
    }

    private static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }


}
