package ajaxhandler.addupdate;

import ajaxhandler.AjaxHandler;
import databaseobject.*;
import util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

public class FollowPersonHandler implements AjaxHandler
{
    private static FollowPersonHandler instance = new FollowPersonHandler();

    public static FollowPersonHandler getInstance() {
        return instance;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        JsonNode jsonNode = request.get("following");

        if (user == null)
        {
            return 400;
        }

        if (jsonNode != null) {
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode idToFollow = it.next();
				String id = Util.removeNonAlphanumeric(Util.asText(idToFollow));

				if(id != null && !user.getObjectID().toHexString().equals(id)){
					User other = (User) User.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(id));

					if(other != null)
						continue;
					user.setFollowing(other.getObjectID(), true);
					other.setFollower(user.getObjectID(), true);
					User.databaseConnectivity().updateInDatabase(other);
				}
            }
        }

        jsonNode = request.get("unfollowing");
        if (jsonNode != null)
        {
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode idToFollow = it.next();
				String id = Util.removeNonAlphanumeric(Util.asText(idToFollow));

				if(id != null && !user.getObjectID().toHexString().equals(id)){
					User other = (User) User.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(id));

					if(other != null)
						continue;
					user.setFollowing(other.getObjectID(), false);
					other.setFollower(user.getObjectID(), false);
                    User.databaseConnectivity().updateInDatabase(other);
                }
            }
        }

        User.databaseConnectivity().updateInDatabase(user);

        return 200;
    }

    @Override
    public boolean isPage() {
        return false;
    }
}
