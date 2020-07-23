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
        /*
        for (int i = 48; i < 58; i++)
        {
            System.out.print("'" + ((char)(i)) + "', " );
        }
        */
        PasswordHasher hasher = PasswordHasher.getSingletonObject();
        String pass1 = "password123";

        String pass2 = "xbobomb";

        String pass1Hash = hasher.createHash(pass1);
        System.out.println("Hash for user1 + pass1: " + pass1Hash);
        if (hasher.createHash(pass1).equals(pass1Hash))
        {
            System.out.println("hashes match!");
        }

        System.out.println("Hash for user2 + pass1: " + hasher.createHash(pass1) );
        System.out.println("Hash for user2 + pass2: " + hasher.createHash(pass2) );
        System.out.println("Hash for user1 + pass12 " + hasher.createHash(pass2) );
    }

    private char[] intToChar =
            {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
            };
    public String createSalt(String message)
    {
        Random r = new Random(stringToLong(message));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++)
        {
            stringBuilder.append(intToChar[r.nextInt(intToChar.length) ] );
        }
        return stringBuilder.toString();
    }

    public Long stringToLong(String message)
    {
        if (message.length() > 5) //12345
        {
            message = message.substring(0,5);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++)
        {
            int charToInt = (int)message.charAt(i);
            builder.append(charToInt);
        }
        return Long.parseLong(builder.toString());
    }

    public String createHash(String message)
    {
        try {
            return hashingAlgorithm(combineSaltPepperAndPass(createSalt(message), message));
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
