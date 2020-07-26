package ajaxhandler.fulfiller;

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

        Activity[] challenges = {};

        for (Activity i : challenges)
        {
            data.put(i.getObjectID().toHexString(), i.toFeedNode());
        }
        response.put("data", data);
        response.put("type", "challengs");
        return 200;

    }

    @Override
    public boolean isPage() {
        return true;
    }
}
