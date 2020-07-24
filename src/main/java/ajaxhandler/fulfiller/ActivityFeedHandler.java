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
import java.util.Comparator;

public class ActivityFeedHandler implements AjaxHandler
{
    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        if (user == null)
        {
            return 400;
        }

        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<String> following = user.getFollowing();
        for (String idString : following)
        {
            User followingUser = (User) User.databaseConnectivity().getFromInfoInDataBase(AjaxHandler.ID, new ObjectId(idString) );
            ArrayList<DBObject> dbObjArr = followingUser.getActivities(50);

            for (DBObject obj : dbObjArr)
            {
                Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, obj.get(ID));
                if (activity != null)
                {
                    activities.add(activity);
                }
            }
        }
        int numToFetch;
        try {
            numToFetch = Integer.parseInt(req.getParameter("top"));
        }
        catch (NumberFormatException e)
        {
            numToFetch = 50;
        }

        String afterKeyword = req.getParameter("after");
        afterKeyword = Util.trimAndnullIfSpecialCharacters(afterKeyword);
        int index = 0;
        activities.sort((Comparator<? super Activity>) activities);
        if (afterKeyword != null) {
            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                if (activity.getObjectID().toHexString().equals(afterKeyword))
                {
                    index = i+1;
                }
            }
        }

        if (index < activities.size())
        {
            if (index+numToFetch < activities.size())
            {
                response.put("activities", Json.toJson(activities.subList(index, index+numToFetch)));
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
        return 200;
    }

    @Override
    public boolean isPage() {
        return false;
    }
}
