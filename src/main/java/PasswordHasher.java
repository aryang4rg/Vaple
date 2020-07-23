import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordHasher
{
    private static PasswordHasher singletonObject = new PasswordHasher();

    public static PasswordHasher getSingletonObject() {
        return singletonObject;
    }

    public String getPepper() {
        return pepper;
    }

    public void setPepper(String pepper) {
        this.pepper = pepper;
    }

    private String pepper = "dQw4w9WgXcQ";
    private PasswordHasher(){}

    public static void main(String... args)
    {
        PasswordHasher hasher = PasswordHasher.getSingletonObject();
        String user1 = "slayer";
        String pass1 = "password123";

        String user2 = "destame";
        String pass2 = "xbobomb";

        String pass1Hash = hasher.createHash(pass1, user1);
        System.out.println("Hash for user1 + pass1: " + pass1Hash);
        if (hasher.createHash(pass1,user1).equals(pass1Hash))
        {
            System.out.println("hashes match!");
        }

        System.out.println("Hash for user2 + pass1: " + hasher.createHash(pass1,user2) );
        System.out.println("Hash for user2 + pass2: " + hasher.createHash(pass2,user2) );
        System.out.println("Hash for user1 + pass12 " + hasher.createHash(pass2,user1) );


    }

    /**
     *
     * @param message password you would want to hash
     * @param salt some type of other unique identifier, such as mongodb _id
     * @return Returns a unique hash version of your message
     */
    public String createHash(String message, String salt)
    {
        try {
            return hashingAlgorithm(combineSaltPepperAndPass(salt, message));
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private String combineSaltPepperAndPass(String salt, String message)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < salt.length() || i < message.length() || i < pepper.length(); i++)
        {
            if (i < salt.length())
            {
                stringBuilder.append(salt.charAt(i));
            }
            if (i < message.length())
            {
                stringBuilder.append(message.charAt(i));
            }
            if (i < pepper.length())
            {
                stringBuilder.append(pepper.charAt(i));
            }
        }

        return stringBuilder.toString();
    }

    //Credit: Baeldung
    private String hashingAlgorithm(String originalString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
                originalString.getBytes(StandardCharsets.UTF_8));

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }



}
