package hu.tilos.radio.backend.util;

import hu.tilos.radio.backend.Configuration;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import javax.inject.Inject;

public class RecaptchaValidator {

    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();

    @Inject
    @Configuration(name = "recaptcha.privatekey")
    private String privateKey;

    public boolean validate(String remoteAddress, String challenge, String solution) {
        reCaptcha.setPrivateKey(privateKey);
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddress, challenge, solution);
        return reCaptchaResponse.isValid();
    }

}
