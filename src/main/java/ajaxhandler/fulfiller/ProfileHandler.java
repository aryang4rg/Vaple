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
public class ProfileHandler implements AjaxHandler
{
	private static ProfileHandler instance = new ProfileHandler();
	public static ProfileHandler getInstance()
	{
		return instance;
	}

	public boolean isPage(){
		return true;
	}

	@Override
	public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {
		if(uriSplit.length == 0)
			return 404;
		if(!ObjectId.isValid(uriSplit[0]))
			return 404;
		if(u.getObjectID().toHexString().equals(uriSplit[0])){
			ObjectNode node = u.toProfileNode();

			node.put("self", true);
			response.put("data", node);
			response.put("type", "profile");

			return 200;
		}

		ObjectId id = new ObjectId(uriSplit[0]);

		User user = (User) User.databaseConnectivity().getFromInfoInDataBase(ID, id);

		if(user == null)
			return 404;
		ObjectNode node = user.toProfileNode();

		if(u.isFollowing(user))
			node.put("following", true);
		response.put("data", node);
		response.put("type", "profile");

		return 200;
	}
}
