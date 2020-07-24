package ajaxhandler.login;

import ajaxhandler.AjaxHandler;
import ajaxhandler.fulfiller.ActivityFeedHandler;
import databaseobject.*;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
	if already logged in redirects person to feed
	if not redirects to log in page
 */
public class LoginHandler implements AjaxHandler
{
	private static LoginHandler instance = new LoginHandler();
	public static LoginHandler getInstance()
	{
		return instance;
	}

	private ActivityFeedHandler handler = new ActivityFeedHandler();
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
