import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class LoginServlet implements AjaxHandler
{

	private static LoginServlet singletonServlet = new LoginServlet();

	public static LoginServlet getSingletonServlet() {
		return singletonServlet;
	}

	private LoginServlet()
	{}

	public boolean isPage() {
		return false;
	}
	public String toValidString(String k){
		if(k == null || k.length() == 0)
			return null;
		// removes all special characters
		k = k.replace("[^a-zA-Z0-9_-]", "");

		if(k.length() == 0)
			return null;
		return k;
	}

	public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User nullUser) throws ServletException, IOException
	{
		if(uriSplit.length > 0)
			return 400;
		String email = Util.nullIfSpecialCharacters(request.get("email").asText());
		String password = Util.nullIfSpecialCharacters(request.get("password").asText());
		if (email == null || password == null || email.length() == 0 || password.length() == 0)
		{
			response.put("token", (Short)null);

			return 200;
		}

		password = PasswordHasher.getSingletonObject().createHash(password);

		//password = PasswordHasher.getSingletonObject().createHash()
		User user = DatabaseConnectivity.getUserByEmail(email);
		if (user == null || !user.getPassword().equals(password))
		{
			response.put("token", (Short)null);
		}
		else
		{
			response.put("account", user.toProfileNode());
			response.put("token", user.getToken());
		}

		return 200;
	}
}
