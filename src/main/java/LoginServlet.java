import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet implements AjaxHandler
{

	private static LoginServlet singletonServlet = new LoginServlet();

	public static LoginServlet getInstance() {
		if (singletonServlet == null)
			singletonServlet = new LoginServlet();

		return singletonServlet;
	}

	private LoginServlet()
	{}

	public boolean isPage() {
		return false;
	}

	public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User nullUser) throws ServletException, IOException
	{
		if(uriSplit.length > 0)
			return 400;
		String email = Util.trimAndnullIfSpecialCharacters(request.get("email").asText());
		String password = Util.trimAndnullIfSpecialCharacters(request.get("password").asText());
		if (email == null || password == null)
		{
			response.put("token", (String)null);

			return 200;
		}

		password = PasswordHasher.getInstance().createHash(password);

		User user = (User) User.databaseConnectivity().getByInfoInDataBase(User.EMAIL, email);
		if (user == null || !user.getPassword().equals(password))
		{
			response.put("token", (String)null);
		}
		else
		{
			response.put("account", user.toProfileNode());
			response.put("token", user.getToken());
		}

		return 200;
	}
}
