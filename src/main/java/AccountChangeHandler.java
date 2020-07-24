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

		String error = null;

		do{
			if(email != null){
				email = Util.trimAndnullIfSpecialCharacters(email);

				if(email == null || !SignUpServlet.isValidEmail(email)){
					error = "Invalid Email";

					break;
				}
			}

			if(password != null){
				password = Util.trimString(password);

				if(password == null){
					error = "Invalid password";

					break;
				}

				password = Util.nullIfSpecialCharacters(password);

				if(password == null){
					error = "Special characters not allowed in password";

					break;
				}

				if(password.length() > 32){
					error = "Password too long";

					break;
				}

				password = PasswordHasher.getInstance().createHash(password);
			}

			if(name != null){
				name = Util.removeTrimAndNonAlphanumeric(name);

				if(name == null){
					error = "Invalid name";

					break;
				}
			}

			if(location_country != null){
				location_country = Util.removeTrimAndNonAlphanumeric(location_country);

				if(location_country == null){
					error = "Invalid country";

					break;
				}
			}

			if(location_state != null){
				location_state = Util.removeTrimAndNonAlphanumeric(location_state);

				if(location_state == null){
					error = "Invalid state or province";

					break;
				}
			}

			if(location_city != null){
				location_city = Util.removeTrimAndNonAlphanumeric(location_city);

				if(location_city == null){
					error = "Invalid city";

					break;
				}
			}

			if(description != null){
				description = description.trim();

				if(description.length() == 0)
					break;
				description = Util.nullIfSpecialCharacters(description);

				if(description == null){
					error = "Special characters not allowed in description";

					break;
				}
			}
		}while(false);

		if(error != null){
			response.put("error", error);

			return 200;
		}

		if(email != null)
			u.setEmail(email);
		if(password != null)
			u.setPassword(password);
		if(name != null)
			u.setName(name);
		if(location_country != null)
			u.setCountry(location_country);
		if(location_state != null)
			u.setState(location_state);
		if(location_city != null)
			u.setCity(location_city);
		if(description != null)
			u.setDescription(description);
		User.getDatabaseConnectivity().updateInDatabase(u);

		ObjectNode node = u.toProfileNode();

		if(picture != null){
			try{
				error = ImageUtil.writeToFile(MainServlet.getFile("/cdn/profiles/" + u.getObjectID().toHexString() + ".png"), data);
			}catch(IOException e){
				node.put("image", "Error creating image");
			}

			if(error != null)
				node.put("image", error);
		}

		response.put("profile", node);

        return 200;
    }
}