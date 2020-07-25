package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.Activity;
import databaseobject.Club;
import databaseobject.User;
import org.bson.types.ObjectId;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ActivityHandler implements AjaxHandler
{
    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        if(uriSplit.length != 3) // 0: / 1: activity 2: id
            return 404;
        String id = uriSplit[2];
        Activity activity = (Activity)Activity.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(id));
        if (activity == null)
        {
            return 404;
        }

        ObjectNode node = Util.createObjectNode();
        response.put("data", node);
        response.put("type", "club");
        response.put("data", activity.toFeedNode());
        return 200;

    }

    @Override
    public boolean isPage() {
        return false;
    }
}
