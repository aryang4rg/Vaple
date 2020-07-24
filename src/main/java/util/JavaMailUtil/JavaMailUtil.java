package util.JavaMailUtil;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailUtil
{
    public static void sendMail(String recipient, String subject, String messageText) throws Exception
    {
        Properties props = new Properties();
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";



        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.transport.protocol", "smtp");


        Password pass = new Password();
        final String myAccountEmail = pass.getEMAIL();
        final String myPassword = pass.getPASSWORD();

        Session session = Session.getInstance(props, new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(myAccountEmail,myPassword);
            }
        });

        Message message = prepareMessage(session,myAccountEmail,recipient,messageText,subject);

        Transport.send(message);

    }

    private static Message prepareMessage(Session session, String myAccountEmail, String recipient, String messageText, String subject)
    {
        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(recipient));
            message.setSubject(subject);
            message.setContent(messageText,"text/html");
            return message;
        } catch(Exception e) {
            Logger.getLogger(JavaMailUtil.class.getName()).log(Level.SEVERE,null,e);
        }

        return null;
    }
}
