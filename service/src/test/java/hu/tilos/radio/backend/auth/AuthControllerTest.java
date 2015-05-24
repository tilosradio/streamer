package hu.tilos.radio.backend.auth;


import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.*;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.util.Map;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class, ValidatorProducer.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class AuthControllerTest {

    @Inject
    AuthService controller;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }


    @Test
    public void testLogin() throws Exception {
        //given
        loadTo(fongoRule, "user", "user-1.json");

        //when
        Map<String, String> login = controller.login(new LoginData("bela", "password"));

        //then
        //System.out.println(AuthController.toSHA1("password" + "d25541250d47c49f20b5243f95dbbd91e4db3d0d"));
        System.out.println(login);
        //System.out.println(response.getEntity().equals("eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJwYXlsb2FkIjoie1widXNlcm5hbWVcIjpcImJlbGFcIn0ifQ.4veHFp-qEiJTAZs20XQ4etcmUeI8cdsoicungVPOm8I"));


    }

    @Test
    public void testLoginFailed() throws Exception {
        //given


        //when
        controller.login(new LoginData("bela", "password2"));

        //then
        Assert.fail();
    }
}