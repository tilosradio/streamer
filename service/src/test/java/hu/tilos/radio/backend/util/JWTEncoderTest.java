package hu.tilos.radio.backend.util;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.data.Token;
import org.junit.Assert;
import org.junit.Test;

public class JWTEncoderTest {

    @Test
    public void testEncode() throws Exception {
        //given
        JWTEncoder encoder = new JWTEncoder();
        encoder.setJwtToken("secret");

        Token token = new Token();
        token.setUsername("user");
        token.setRole(Role.ADMIN);

        //when
        String encoded = encoder.encode(token);
        System.out.println(encoded);
        Token result = encoder.decode(encoded);

        //then
        Assert.assertEquals("user", token.getUsername());
    }
}