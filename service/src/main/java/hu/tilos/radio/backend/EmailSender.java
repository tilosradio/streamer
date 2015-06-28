package hu.tilos.radio.backend;


import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class EmailSender {

    private static Logger LOG = LoggerFactory.getLogger(EmailSender.class);

    @Inject
    @Configuration(name = "mandrill.key")
    private String key;

    public EmailSender() {
    }

    public void send(Email email) {
        try {
            MandrillApi api = new MandrillApi(key);
            MandrillMessage message = new MandrillMessage();
            message.setSubject(email.getSubject());
            message.setText(email.getBody());

            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
            recipient.setEmail(email.getTo());
            List<MandrillMessage.Recipient> recipients = new ArrayList<>();
            recipients.add(recipient);
            message.setTo(recipients);
            message.setFromEmail("noreply@tilos.hu");
            message.setFromName("Tilos szervergép");

            message.setPreserveRecipients(false);
            MandrillMessageStatus[] statuses = api.messages().send(message, false);
            for (MandrillMessageStatus status : statuses) {
                if (!"sent".equals(status.getStatus())) {
                    throw new RuntimeException("Can't send the email: " + status.getRejectReason());
                }
                LOG.debug(status.getStatus());
                LOG.debug(status.getRejectReason());
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't send email", e);
        }

    }
}
