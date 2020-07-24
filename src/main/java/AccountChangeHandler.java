import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

public class AccountChangeHandler implements AjaxHandler
{
    public boolean isPage(){
        return false;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {
		if(uriSplit.length > 0)
			return 404;
		if(u == null)
			return 400;
		String email = request.get("email").asText();
		String name = request.get("name").asText();
		String password = request.get("password").asText();
		String location_country = request.get("location_country").asText();
		String location_state = request.get("location_state").asText();
		String location_city = request.get("location_city").asText();
		String description = request.get("description").asText();
		String picture = request.get("image").asText();



		String[] base64Image = data.split(",");

		String image = data[1];
		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

        return 200;
    }
}