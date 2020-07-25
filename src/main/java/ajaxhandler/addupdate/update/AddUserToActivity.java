package ajaxhandler.addupdate.update;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.Activity;
import databaseobject.User;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddUserToActivity implements AjaxHandler {

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        String id = Util.asText(request.get("activity"));
        Boolean toAdd =  request.get("addToActivity").asBoolean();
        if (id == null)
        {
            return 400;
        }
        if (toAdd == null)
        {
            return 400;
        }
        Activity activity = (Activity) Activity.databaseConnectivity().getFromInfoInDataBase(ID, id);
        if (activity == null)
        {
            return 400;
        }
        user.setActivities(activity.getObjectID(), toAdd);
        //activity.get
        return 400;
    }

    @Override
    public boolean isPage() {
        return false;
    }
}
