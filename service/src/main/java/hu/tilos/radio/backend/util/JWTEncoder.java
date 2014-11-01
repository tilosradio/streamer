package hu.tilos.radio.backend.util;

import com.google.gson.Gson;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import hu.tilos.radio.backend.data.Token;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import javax.inject.Inject;

public class JWTEncoder {

    @Inject
    @ConfigProperty(name = "jwt.secret")
    private String jwtToken;

    private Gson gson = new Gson();

    public String encode(Token token) {
        try {

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWS).build();

            Payload payload = new Payload(gson.toJson(token));
            // Create JWS object
            JWSObject jwsObject = new JWSObject(header, payload);

            // Create HMAC signer
            JWSSigner signer = new MACSigner(jwtToken.getBytes());

            jwsObject.sign(signer);

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    public Token decode(String bearer) {
        boolean verifiedSignature;
        JWSObject jwsObject = null;
        try {
            jwsObject = JWSObject.parse(bearer);

            JWSVerifier verifier = new MACVerifier(jwtToken.getBytes());

            verifiedSignature = jwsObject.verify(verifier);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


        if (!verifiedSignature)
            throw new RuntimeException("JWS Object signature can't be verified");


        return gson.fromJson(jwsObject.getPayload().toString(), Token.class);


    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
