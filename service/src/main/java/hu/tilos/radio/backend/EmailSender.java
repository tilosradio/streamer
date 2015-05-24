package hu.tilos.radio.backend;

//import javax.annotation.Resource;
//import javax.mail.Address;
//import javax.mail.Message;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

public class EmailSender {


    public void send(Email email) {
        try {
//            Message message = new MimeMessage(mailSessin);
//            message.setFrom(new InternetAddress(email.getFrom()));
//            Address toAddress = new InternetAddress(email.getTo());
//            message.addRecipient(Message.RecipientType.TO, toAddress);
//            message.setSubject(email.getSubject());
//            message.setContent(email.getBody(), "text/plain");
//            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Can't send email", e);
        }

    }
}
