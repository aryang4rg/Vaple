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
import java.util.List;
import java.util.Random;

import static databaseobject.Activity.*;

public class ExploreFeedHandler implements AjaxHandler
{

    private ExploreFeedHandler(){}
    private static ExploreFeedHandler instance = new ExploreFeedHandler();
    public static ExploreFeedHandler getInstance()
    {
        return instance;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException
    {
        String tag = req.getParameter("tag");
        response.put("type", "explore");
        ObjectNode data = Util.createObjectNode();
        tag = Util.trimAndnullIfSpecialCharacters(tag);
        
        data.put("tag", tag);

        ObjectNode activityNode = Util.createObjectNode();
        ObjectNode clubNode = Util.createObjectNode();
        ObjectNode userNode = Util.createObjectNode();

        if (tag != null)
        {
            List<DBObject> activities = DatabaseConnectivity.getAllObjectsByProperty(tag, DatabaseConnectivity.ACTIVITYCOLLECTION);
            List<DBObject> clubs = DatabaseConnectivity.getAllObjectsByProperty(tag, DatabaseConnectivity.CLUBCOLLECTION);
            List<DBObject> users = DatabaseConnectivity.getAllObjectsByProperty(tag, DatabaseConnectivity.ACCOUNTCOLLECTION);



            for (DBObject obj: activities)
            {
                ObjectId id = (ObjectId) obj.get(ID);
                Activity myActivity = (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, id);
                if (myActivity != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)myActivity.get(ID)).toHexString());
                    innerNode.put("name", (String)myActivity.get(NAME));
                    innerNode.put("description", (String)myActivity.get(DESCRIPTION));
                    innerNode.put("attending_number", myActivity.allAttending().size());
                    innerNode.put("type", (String)myActivity.get(TYPE));
                    innerNode.put("is_past", ( System.currentTimeMillis() > (Long)myActivity.get(TIME_START)  ));
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
                    innerNode.put("description", (String)club.get(DESCRIPTION));
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
                    innerNode.put("description", (String)myUser.get(DESCRIPTION));
                    userNode.put( ((ObjectId)myUser.get(ID)).toHexString(), innerNode);
                }
            }

        }
        else
        {
            List<DBObject> activities = DatabaseConnectivity.ACTIVITYCOLLECTION.find().toArray();
            List<DBObject> clubs =  DatabaseConnectivity.CLUBCOLLECTION.find().toArray();
            List<DBObject> users = DatabaseConnectivity.ACCOUNTCOLLECTION.find().toArray();

            Random r = new Random();

            if (activities.size() >= 2)
            {
                DBObject one = activities.remove(r.nextInt(activities.size()));
                Activity oneAct = (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, one.get(ID));
                DBObject two = activities.remove(r.nextInt(activities.size()));
                Activity twoAct = (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, two.get(ID));
                if (oneAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)oneAct.get(ID)).toHexString());
                    innerNode.put("name", (String)oneAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));
                    innerNode.put("attending_number", oneAct.allAttending().size());
                    innerNode.put("type", (String)oneAct.get(TYPE));
                    innerNode.put("is_past", ( System.currentTimeMillis() > (Long)oneAct.get(TIME_START)  ));

                    activityNode.put( ((ObjectId)oneAct.get(ID)).toHexString(), innerNode);
                }
                if (twoAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)twoAct.get(ID)).toHexString());
                    innerNode.put("name", (String)twoAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));
                    innerNode.put("attending_number", twoAct.allAttending().size());
                    innerNode.put("type", (String)twoAct.get(TYPE));
                    innerNode.put("is_past", ( System.currentTimeMillis() > (Long)twoAct.get(TIME_START)  ));

                    activityNode.put( ((ObjectId)twoAct.get(ID)).toHexString(), innerNode);
                }

            }
            else if (activities.size() == 1)
            {
                DBObject one = activities.remove(r.nextInt(activities.size()));
                Activity oneAct = (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, one.get(ID));
                if (oneAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)oneAct.get(ID)).toHexString());
                    innerNode.put("name", (String)oneAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));
                    innerNode.put("attending_number", oneAct.allAttending().size());
                    innerNode.put("type", (String)oneAct.get(TYPE));
                    innerNode.put("is_past", ( System.currentTimeMillis() > (Long)oneAct.get(TIME_START)  ));
                    activityNode.put( ((ObjectId)oneAct.get(ID)).toHexString(), innerNode);
                }
            }

            if (users.size() >= 2)
            {
                DBObject one = users.remove(r.nextInt(users.size()));
                User oneAct = (User)User.databaseConnectivity().getFromInfoInDataBase(ID, one.get(ID));
                DBObject two = users.remove(r.nextInt(users.size()));
                User twoAct = (User)User.databaseConnectivity().getFromInfoInDataBase(ID, two.get(ID));
                if (oneAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)oneAct.get(ID)).toHexString());
                    innerNode.put("name", (String)oneAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));

                    userNode.put( ((ObjectId)oneAct.get(ID)).toHexString(), innerNode);
                }
                if (twoAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)twoAct.get(ID)).toHexString());
                    innerNode.put("name", (String)twoAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));

                    userNode.put( ((ObjectId)twoAct.get(ID)).toHexString(), innerNode);
                }

            }
            else if (users.size() == 1)
            {
                DBObject one = users.remove(r.nextInt(users.size()));
                User oneAct = (User)User.databaseConnectivity().getFromInfoInDataBase(ID, one.get(ID));
                if (oneAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)oneAct.get(ID)).toHexString());
                    innerNode.put("name", (String)oneAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));

                    userNode.put( ((ObjectId)oneAct.get(ID)).toHexString(), innerNode);
                }
            }
            if (clubs.size() >= 2)
            {
                DBObject one = clubs.remove(r.nextInt(clubs.size()));
                Club oneAct = (Club)Club.databaseConnectivity().getFromInfoInDataBase(ID, one.get(ID));
                DBObject two = clubs.remove(r.nextInt(clubs.size()));
                Club twoAct = (Club)Club.databaseConnectivity().getFromInfoInDataBase(ID, two.get(ID));
                if (oneAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)oneAct.get(ID)).toHexString());
                    innerNode.put("name", (String)oneAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));
                    clubNode.put( ((ObjectId)oneAct.get(ID)).toHexString(), innerNode);
                }
                if (twoAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)twoAct.get(ID)).toHexString());
                    innerNode.put("name", (String)twoAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));
                    clubNode.put( ((ObjectId)twoAct.get(ID)).toHexString(), innerNode);
                }

            }
            else if (clubs.size() == 1)
            {
                DBObject one = clubs.remove(r.nextInt(clubs.size()));
                Club oneAct = (Club)Club.databaseConnectivity().getFromInfoInDataBase(ID, one.get(ID));
                if (oneAct != null)
                {
                    ObjectNode innerNode = Util.createObjectNode();
                    innerNode.put("id", ((ObjectId)oneAct.get(ID)).toHexString());
                    innerNode.put("name", (String)oneAct.get(NAME));
                    innerNode.put("description", (String)oneAct.get(DESCRIPTION));
                    clubNode.put( ((ObjectId)oneAct.get(ID)).toHexString(), innerNode);
                }
            }


        }

        data.put("activities", activityNode);
        data.put("clubs", clubNode);
        data.put("users", userNode);
        response.put("data", data);
        return 200;
    }

    @Override
    public boolean isPage()
    {
        return true;
    }
}
