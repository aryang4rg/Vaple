package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import databaseobject.*;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

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
			ObjectNode clubs = new ObjectNode(JsonNodeFactory.instance);
			ArrayList<String> ClubIds = User.dbObjectClubListToArrayList( u.getClubs());
			for (String id : ClubIds)
			{
				Club club = (Club)Club.databaseConnectivity().getFromInfoInDataBase(ID, id);
				if (club != null)
				{
					ObjectNode myClubNode = Util.createObjectNode();
					myClubNode.put("id", club.getObjectID().toHexString());
					myClubNode.put("name", (String)club.get(Club.NAME));
			clubs.put(club.getObjectID().toHexString(), myClubNode);
			}
			}
			ObjectNode data = Util.createObjectNode();
			data.put("clubs", clubs);
			response.put("data", data);
			return 200;
		}

}
