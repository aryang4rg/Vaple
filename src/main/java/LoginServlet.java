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
	public boolean isPage(){
		return false;
	}

	public void service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response) throws ServletException, IOException
	{
		String email = Util.nullIfSpecialCharacters(request.get("email").asText());
		String password = Util.nullIfSpecialCharacters(request.get("password").asText());
		//TODO add encryption to password
		if (email == null || password == null)
		{
			response.put("token", null);

			return;
		}

		User user = DatabaseConnectivity.getUserByEmail(email);
		if (user == null || !user.getPassword().equals(password))
		{
			response.put("token", null);
		}
		else
		{
			response.put("account", user.toObjectNode());
			response.put("token", user.getToken());
		}
	}
}
