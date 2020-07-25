package ajaxhandler.login;

import ajaxhandler.AjaxHandler;
import ajaxhandler.fulfiller.ActivityFeedHandler;
import ajaxhandler.fulfiller.NewClubHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnverifiedHandler implements AjaxHandler
{

    private UnverifiedHandler(){}

    private static UnverifiedHandler instance = new UnverifiedHandler();
    public static UnverifiedHandler getInstance()
    {
        return instance;
    }

    public boolean isPage(){
        return true;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {

        if(uriSplit.length > 0)
            return 404;

        response.put("type", "unverified");

        return 200;
    }
}
