package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import databaseobject.*;
import util.*;

import databaseobject.Activity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ActivityFeedHandler implements AjaxHandler
{
    boolean isAPage;

    public ActivityFeedHandler(boolean isAPage)
    {
        this.isAPage = isAPage;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {

        int numToFetch;
        String specificUserId = req.getParameter("id");
        int index = 0;

        String afterKeyword = req.getParameter("after");
        afterKeyword = Util.trimAndnullIfSpecialCharacters(afterKeyword);
        ArrayList<Activity> activities = new ArrayList<>();

        try {
            numToFetch = Integer.parseInt(req.getParameter("top"));
        }
        catch (NumberFormatException e)
        {
            numToFetch = 50;
        }

        if (specificUserId != null)
        {
            User u =(User) User.databaseConnectivity().getFromInfoInDataBase(ID,new ObjectId(specificUserId));
            if (u == null)
            {
                return 400;
            }


            ArrayList<DBObject> activitiesDBObject = u.getActivities();
            for (DBObject obj : activitiesDBObject) {
                Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, obj.get(ID));
                if (activity != null) {
                    activities.add(activity);
                }
            }


        }


        else {
            if (user == null)
            {
                return 400;
            }
            ArrayList<String> following = user.getFollowing();
            for (String idString : following) {
                User followingUser = (User) User.databaseConnectivity().getFromInfoInDataBase(AjaxHandler.ID, new ObjectId(idString));
                if (followingUser == null) {
                    continue;
                }
                ArrayList<DBObject> dbObjArr = followingUser.getActivities(50);

                for (DBObject obj : dbObjArr) {
                    Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, obj.get(ID));
                    if (activity != null) {
                        activities.add(activity);
                    }
                }
            }
        }


        numToFetch = Math.abs(numToFetch);
        Collections.sort(activities);
        if (afterKeyword != null) {
            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                if (activity.getObjectID().toHexString().equals(afterKeyword))
                {
                    index = i+1;
                    break;
                }
            }
        }

        if (index < activities.size())
        {
            if (index+numToFetch < activities.size())
            {
                ArrayList<JsonNode> jsonNodeHolderForActivities = new ArrayList<>();
                for (int i = index; i < index+numToFetch; i++)
                {
                    jsonNodeHolderForActivities.add(activities.get(i).toFeedNode());
                }
                response.put("activities", Json.toJson(jsonNodeHolderForActivities));
            }
            else
            {
                response.put("activities", Json.toJson(activities.subList(index, activities.size())));
            }
        }
        else
        {
            response.put("activities", Json.toJson(new ArrayList<Activity>()));
        }
        response.put("type", "feed");
        return 200;
    }

    @Override
    public boolean isPage() {
        return true;
    }
}
