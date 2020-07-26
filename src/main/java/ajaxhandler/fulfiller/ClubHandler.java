package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.Club;
import databaseobject.User;
import org.bson.types.ObjectId;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClubHandler implements AjaxHandler
{
    private static ClubHandler instance = new ClubHandler();
    private ClubHandler(){}

    public static ClubHandler getInstance() {
        return instance;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {

        if(uriSplit.length != 1) // 0: / 1: club 2: id
            return 404;
        String id = uriSplit[0];
        if (id == null || id.equals("undefined") || id.equals("null"))
        {
            return 404;
        }
        Club c;

        if(!ObjectId.isValid(id))
        {
            return 404;
        }

        c = (Club) Club.databaseConnectivity().getFromInfoInDataBase(ID, new ObjectId(id));


        if (c == null)
        {
            return 404;
        }

        ObjectNode node = Util.createObjectNode();
        response.put("data", node);
        response.put("type", "club");
        ObjectNode data = c.viewClubHandlerJson();

        if (user == null)
        {
            data.put("owner", false);
            data.put("joined", false);
        }
        else
        {
           if(c.get("owner").equals(user.getObjectID().toHexString()))
           {
               data.put("owner",true);
               data.put("joined", false);
           }
           else if (user.getClubList().contains(c.getObjectID().toHexString()))
           {
               data.put("owner",false);
               data.put("joined", true);
           }
           else
           {
               data.put("owner",false);
               data.put("joined", false);
           }
        }

        response.put("data", data);
        return 200;

    }

    @Override
    public boolean isPage() {
        return true;
    }
}
