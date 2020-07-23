import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet implements AjaxHandler
{
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonNode node = Json.parse(req.getReader());
        String email = node.get("email").asText();
        String name = node.get("name").asText();
        String password = node.get("password").asText();
        String location_country = node.get("location_country").asText();
        String location_state = node.get("location_state").asText();
        String location_city = node.get("location_city").asText();

        //TODO make token, verify fields exist
       // User user = new User(name,email,password, location_country, location_state, location_city, );


    }
}
