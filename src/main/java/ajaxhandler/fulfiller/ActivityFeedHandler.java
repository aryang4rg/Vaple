package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.mongodb.BasicDBObject;
import databaseobject.*;
import util.*;

import databaseobject.Activity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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

        String clubParameterId = req.getParameter("clubId");

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
                return 404;
            }


            ArrayList<String> activitiesDBObject = u.getActivitiesList();
            for (String obj : activitiesDBObject) {
                Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(obj));
                if (activity != null) {
                    activities.add(activity);
                }
            }


        }
        else if (clubParameterId != null)
        {
            if (!ObjectId.isValid(clubParameterId))
                return 400;
            Club c = (Club)Club.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(clubParameterId));
            if (c == null) {
                return 404;
            }
            ArrayList<String> activitiesInClub = c.getActivities();

            for (String ActivityID : activitiesInClub) {
                Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(ActivityID));
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
            for (String userId : following) {
                User followingUser = (User) User.databaseConnectivity().getFromInfoInDataBase(AjaxHandler.ID, new ObjectId(userId));
                if (followingUser == null) {
                    continue;
                }
                ArrayList<String> dbObjArr = followingUser.getActivitiesList();

                for (String activityId : dbObjArr) {
                    Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(activityId));
                    if (activity != null) {
                        activities.add(activity);
                    }
                }
            }

            ArrayList<String> clubs = user.getClubList();
            for (String clubId : clubs) {
                Club myClub = (Club) Club.databaseConnectivity().getFromInfoInDataBase(AjaxHandler.ID, new ObjectId(clubId));
                if (myClub == null) {
                    continue;
                }
                ArrayList<String> activitiesInClub = myClub.getActivities();

                for (String ActivityID : activitiesInClub) {
                    Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(ActivityID));
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

		BasicDBObject obj = new BasicDBObject();
		ArrayList<JsonNode> jsonNodeHolderForActivities = new ArrayList<>();
        ObjectNode data = Util.createObjectNode();
        if (index < activities.size())
        {
            if (index+numToFetch < activities.size())
            {

                for (int i = index; i < index+numToFetch; i++)
                {
					Activity activity = activities.get(i);

					obj.put(activity.getObjectID().toHexString(), activity.toFeedNode());
                }
            }
            else
            {
                for (int i = index; i < activities.size(); i++)
                {
					Activity activity = activities.get(i);
					obj.put(activity.getObjectID().toHexString(), activity.toFeedNode());
                }
            }
		}

		for(String str : obj.keySet())
			jsonNodeHolderForActivities.add( (JsonNode)obj.get(str));
		data.put("activities", Json.toJson(jsonNodeHolderForActivities));

        if(isAPage){
			response.put("type", "feed");
			response.put("data", data);
		}else{
			response.put("activities", data.get("activities"));
		}
        return 200;
    }

    @Override
    public boolean isPage() {
        return isAPage;
    }
}
