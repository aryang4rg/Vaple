import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginHandler implements AjaxHandler
{
	private static LoginHandler instance = new LoginHandler();
	public static LoginHandler getInstance()
	{
		return instance;
	}

	private FeedHandler handler = new FeedHandler();
	public boolean isPage(){
		return true;
	}

	@Override
	public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {
		if(uriSplit.length > 0)
			return 404;
		if(u != null)
			return handler.service(req, resp, request, response, new String[]{}, u);
		response.put("type", "login");

		return 200;
	}
}
