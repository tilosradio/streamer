package hu.tilos.radio.backend.util;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import javax.inject.Inject;

public class RecaptchaValidator {

    @Inject
    @ConfigProperty(name = "recaptcha.privatekey")
    private String privateKey;

    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();

    public boolean validate(String remoteAddress, String challenge, String solution){
        reCaptcha.setPrivateKey(privateKey);
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddress, challenge, solution);
        return reCaptchaResponse.isValid();
    }

}
