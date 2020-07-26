package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.Activity;
import databaseobject.User;
import org.bson.types.ObjectId;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChallengesHandler implements AjaxHandler
{
    private static ChallengesHandler instance = new ChallengesHandler();
    private ChallengesHandler(){}

    public static ChallengesHandler getInstance() {
        return instance;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        ObjectNode data = Util.createObjectNode();

        Activity[] challenges = {
                (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId("5f1d1db564b99c5008590e67")),
                (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId("5f1d1db564b99c5008590e67")),
                (Activity)  Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId("5f1d1db564b99c5008590e67")),
                (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId("5f1d1db564b99c5008590e67"))

                                };

        for (Activity i : challenges)
        {
            data.put(i.getObjectID().toHexString(), i.toFeedNode());
        }
        ObjectNode activities = Util.createObjectNode();
        activities.put("activities", data);
        response.put("data", activities);
        response.put("type", "challenges");
        return 200;

    }

    @Override
    public boolean isPage() {
        return true;
    }
}
