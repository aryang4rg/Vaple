package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.Club;
import databaseobject.User;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class NewClubHandler implements AjaxHandler
{
    private static NewClubHandler instance = new NewClubHandler();
    public static NewClubHandler getInstance()
    {
        return instance;
    }

    public boolean isPage(){
        return true;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException
    {
        if(u == null)
            return 400;
        response.put("type", "new_club");
        return 200;
    }
}
