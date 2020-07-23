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

public class SignUpServlet implements AjaxHandler
{
	public String toValidString(String k){
		if(k == null || k.length() == 0)
			return null;
		/* removes all special characters */
		k = k.replace("[^a-zA-Z0-9_-]", "");

		if(k.length() == 0)
			return null;
		return k;
	}

	public boolean isValidEmail(String email){
		if(email.indexOf(' ') != -1)
			return false;
		int pi = email.lastIndexOf('.');
		int ai = email.indexOf('@');

		if(ai < 1 || pi <= ai + 1 || pi + 1 >= email.length())
			return false;
		return true;
	}

	public boolean isValidPassword(String k){
		return k.length() <= 32;
	}

	private String generateSafeToken() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[64];
		random.nextBytes(bytes);
		Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		String token = encoder.encodeToString(bytes);
		return token;
	}

	public boolean isPage(){
		return false;
	}

	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response) throws ServletException, IOException {
		String email = Util.nullIfSpecialCharacters(request.get("email").asText());
		String name = Util.removeTrimAndNonAlphanumeric(request.get("name").asText());
		String password = Util.nullIfSpecialCharacters(request.get("password").asText());
		String location_country = Util.removeTrimAndNonAlphanumeric(request.get("location_country").asText());
		String location_state = Util.removeTrimAndNonAlphanumeric(request.get("location_state").asText());
		String location_city = Util.removeTrimAndNonAlphanumeric(request.get("location_city").asText());

		if(email == null || name == null || password == null || location_country == null ||
			location_state == null || location_city == null){
				response.put("token", null);
				response.put("error", "Invalid field(s)");

				return;
			}
		if(!isValidEmail(email)){
			response.put("token", null);
			response.put("error", "Invalid email");

			return;
		}

		if(!isValidPassword(password)){
			response.put("token", null);
			response.put("error", "Password must be less than 32 characters");

			return;
		}

		if(DatabaseConnectivity.emailAlreadyExists(email)){
			response.put("token", null);
			response.put("error", "Email already in use");

			return;
		}

		/* here is where you'd encrypt the password */

		for(int i = 0; i < 100; i++){
			String token = generateSafeToken();

			if(DatabaseConnectivity.getUserByToken(token) == null){
				User user = new User(name, email, password, location_country, location_state, location_city, "", token);

				DatabaseConnectivity.addNewUser(user);

				response.put("account", user.toAccountNode());
				response.put("token", user.getToken());

				return;
			}
		}

		response.put("token", null);
		response.put("error", "Server is busy, try again later");
	}
}
