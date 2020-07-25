package ajaxhandler.addupdate.update;

import ajaxhandler.AjaxHandler;
import databaseobject.*;
import util.*;

import databaseobject.Club;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClubFollowHandler implements AjaxHandler
{
    private static ClubFollowHandler instance = new ClubFollowHandler();

    public static ClubFollowHandler getInstance() {
        return instance;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        String club_id = Util.asText(request.get("club_id"));
        Boolean toJoin = request.get("toJoin").asBoolean();

        if (Util.trimAndnullIfSpecialCharacters(club_id) == null)
        {
            return 400;
        }
        if (toJoin == null)
        {
            return 400;
        }

        Club c = (Club) Club.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(club_id));
        if (c == null)
        {
            return 400;
        }

        user.setClubs(c.getObjectID(), toJoin);
        c.setMember(user.getObjectID(), toJoin);
        Club.databaseConnectivity().updateInDatabase(c);
        User.databaseConnectivity().updateInDatabase(user);
        return 200;
    }

    @Override
    public boolean isPage() {
        return false;
    }
}
