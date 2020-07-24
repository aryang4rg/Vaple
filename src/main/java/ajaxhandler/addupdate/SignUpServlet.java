package ajaxhandler.addupdate;

import ajaxhandler.AjaxHandler;
import databaseobject.*;
import util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import util.PasswordHasher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class SignUpServlet implements AjaxHandler
{

	private static SignUpServlet singletonServlet = new SignUpServlet();
	public static SignUpServlet getInstance()
	{
		if (singletonServlet == null)
			singletonServlet = new SignUpServlet();
		return singletonServlet;

	}

	private SignUpServlet(){}

	public static boolean isValidEmail(String email){
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
	public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {
		if(uriSplit.length > 0)
			return 404;
		if(u != null)
			return 400;
		String email = Util.trimAndnullIfSpecialCharacters(Util.asText(request.get("email")));
		String name = Util.removeTrimAndNonAlphanumeric(Util.asText(request.get("name")));
		String password = Util.trimAndnullIfSpecialCharacters(Util.asText(request.get("password")));
		String location_country = Util.removeTrimAndNonAlphanumeric(Util.asText(request.get("location_country")));
		String location_state = Util.removeTrimAndNonAlphanumeric(Util.asText(request.get("location_state")));
		String location_city = Util.removeTrimAndNonAlphanumeric(Util.asText(request.get("location_city")));

		if(email == null || name == null || password == null || location_country == null ||
			location_state == null || location_city == null){
				response.put("token", (String)null);
				response.put("error", "Invalid field(s)");

				return 200;
			}
		if(!isValidEmail(email)){
			response.put("token", (String)null);
			response.put("error", "Invalid email");

			return 200;
		}

		if(!isValidPassword(password)){
			response.put("token", (String)null);
			response.put("error", "Password must be less than 32 characters");

			return 200;
		}

		if(User.databaseConnectivity().infoExistsInDatabase(User.EMAIL, email)){
			response.put("token", (String)null);
			response.put("error", "Email already in use");

			return 200;
		}

		password = PasswordHasher.getInstance().createHash(password);

		for(int i = 0; i < 100; i++){
			String token = generateSafeToken();

			if(!User.databaseConnectivity().infoExistsInDatabase(User.TOKEN, token)){
				User user = new User(name, email, password, location_country, location_state, location_city, "", token);

				User.databaseConnectivity().addInDatabase(user);

				response.put("account", user.toAccountNode());
				response.put("token", user.getToken());

				return 200;
			}
		}

		response.put("token", (String)null);
		response.put("error", "Server is busy, try again later");

		return 200;
	}
}
