package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.DBObject;
import databaseobject.Activity;
import databaseobject.Club;
import databaseobject.User;
import main.DatabaseConnectivity;
import org.bson.types.ObjectId;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static databaseobject.Activity.NAME;
import static databaseobject.Activity.getActivitiesByTag;

public class ExploreFeedHandler implements AjaxHandler
{

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException
    {
        String tag = Util.asText(request.get("tag"));
        tag = Util.trimAndnullIfSpecialCharacters(tag);
        if (tag != null)
        {
            List<DBObject> activities = DatabaseConnectivity.getAllObjectsByProperty(tag, DatabaseConnectivity.ACTIVITYCOLLECTION);
            List<DBObject> clubs = DatabaseConnectivity.getAllObjectsByProperty(tag, DatabaseConnectivity.CLUBCOLLECTION);
            List<DBObject> users = DatabaseConnectivity.getAllObjectsByProperty(tag, DatabaseConnectivity.ACCOUNTCOLLECTION);

            ObjectNode activityNode = Util.createObjectNode();
            ObjectNode clubNode = Util.createObjectNode();
            ObjectNode userNode = Util.createObjectNode();

            for (DBObject obj: activities)
            {
                ObjectId id = (ObjectId) obj.get(ID);
                Activity myActivity = (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, id);
                if (myActivity != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)myActivity.get(ID)).toHexString());
                    innerNode.put("name", (String)myActivity.get(NAME));
                    activityNode.put( ((ObjectId)myActivity.get(ID)).toHexString(), innerNode);
                }
            }

            for (DBObject obj: clubs)
            {
                ObjectId id = (ObjectId) obj.get(ID);
                Club club = (Club)Club.databaseConnectivity().getFromInfoInDataBase(ID, id);
                if (club != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)club.get(ID)).toHexString());
                    innerNode.put("name", (String)club.get(NAME));
                    clubNode.put( ((ObjectId)club.get(ID)).toHexString(), innerNode);
                }
            }


            for (DBObject obj: users)
            {
                ObjectId id = (ObjectId) obj.get(ID);
                User myUser = (User)User.databaseConnectivity().getFromInfoInDataBase(ID, id);
                if (myUser != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)myUser.get(ID)).toHexString());
                    innerNode.put("name", (String)myUser.get(NAME));
                    userNode.put( ((ObjectId)myUser.get(ID)).toHexString(), innerNode);
                }
            }
            response.put("activities", activityNode);
            response.put("clubs", clubNode);
            response.put("users", userNode);
        }
        else
        {
            
        }
        return 0;
    }

    @Override
    public boolean isPage()
    {
        return true;
    }
}
