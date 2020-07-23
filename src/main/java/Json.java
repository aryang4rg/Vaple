import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.TreeNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class Json
{
    public static void main(String[] args) throws IOException
    {
        String jsonSource = "{\"email\":\"agarg.usa@gmail.com\",\"password\":\"agargusa6969\"}\n";
        JsonNode node =  Json.parse(jsonSource);
        System.out.println(node.get("email").toString());
        TreeNode treeNode;
        JsonTest test = new JsonTest("this is my name", "pass123", "hexadec", "token", 69);
        JsonNode nodeTest = toJson(test);
        System.out.println(stringify(nodeTest));
       // System.out.println(node.toString());

    }
    public static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper defObjectMapper = new ObjectMapper();
        defObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return defObjectMapper;
    }

    private static ObjectMapper objectMapper = getDefaultObjectMapper();

    public static JsonNode parse(String src) throws IOException
    {
        return  objectMapper.readTree(src);
    }

    public static JsonNode parse(Reader src) throws IOException
    {
        return  objectMapper.readTree(src);
    }

    public static JsonNode parse(InputStream src) throws IOException
    {
        return  objectMapper.readTree(src);
    }




    public static <A> A fromJson(JsonNode node, Class<A> clazz) throws JsonProcessingException {
        return objectMapper.treeToValue(node, clazz);
    }

    public static JsonNode toJson(Object a)
    {
        return objectMapper.valueToTree(a);
    }

    public static String stringify(JsonNode node) throws JsonProcessingException {
        return objectMapper.writer().writeValueAsString(node);
    }

    public static String prettyPrint(JsonNode node) throws JsonProcessingException {
        ObjectWriter objectWriter = objectMapper.writer();
        objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
        return objectWriter.writeValueAsString(node);
    }

}
