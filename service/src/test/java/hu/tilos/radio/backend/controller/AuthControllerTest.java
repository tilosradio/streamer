package hu.tilos.radio.backend.controller;


import hu.tilos.radio.backend.ConfigurationProducer;
import hu.tilos.radio.backend.TestConfigProvider;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.ValidatorProducer;
import hu.tilos.radio.backend.controller.AuthController;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.output.LoginData;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class, TestConfigProvider.class, ValidatorProducer.class, ConfigurationProducer.class})
@ActivatedAlternatives(TestConfigProvider.class)
public class AuthControllerTest {

    @Inject
    AuthController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }


    @Test
    public void testLogin() throws Exception {
        //given


        //when
        Response response = controller.login(new LoginData("bela", "password"));

        //then
        //System.out.println(AuthController.toSHA1("password" + "d25541250d47c49f20b5243f95dbbd91e4db3d0d"));
        Assert.assertEquals(200, response.getStatus());
        System.out.println(response.getEntity().equals("eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJwYXlsb2FkIjoie1widXNlcm5hbWVcIjpcImJlbGFcIn0ifQ.4veHFp-qEiJTAZs20XQ4etcmUeI8cdsoicungVPOm8I"));


    }

    @Test
    public void testLoginFailed() throws Exception {
        //given


        //when
        Response response = controller.login(new LoginData("bela", "password2"));

        //then
        Assert.assertEquals(403, response.getStatus());
    }
}