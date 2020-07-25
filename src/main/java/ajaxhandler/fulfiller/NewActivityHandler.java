package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import databaseobject.*;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Gives profile information given id
 */
public class NewActivityHandler implements AjaxHandler
{
	private static NewActivityHandler instance = new NewActivityHandler();
	public static NewActivityHandler getInstance()
	{
		return instance;
	}

	public boolean isPage(){
		return true;
	}

	@Override
	public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {
		if(uriSplit.length > 0)
			return 404;
		if(u == null)
			return 400;
		response.put("type", "new_activity");

		return 200;
	}
}
