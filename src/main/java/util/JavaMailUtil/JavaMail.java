package util.JavaMailUtil;

public class JavaMail
{
    /**
     * Send a message
     *
     * @param recipient   The recipient of the message (1 user at a time as of right now)
     * @param subject     The subject of the message
     * @param messageText The body text of the message
     */
    public static void sendMessage(String recipient, String subject, String messageText) throws Exception
    {
        JavaMailUtil.sendMail(recipient, subject, messageText);
    }

}
