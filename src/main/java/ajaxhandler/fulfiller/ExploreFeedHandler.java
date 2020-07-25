package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExploreFeedHandler implements AjaxHandler
{

     boolean isPage;

     public ExploreFeedHandler(boolean isPage)
     {
         this.isPage = isPage;
     }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException
    {
        return 400;
    }

    @Override
    public boolean isPage()
    {
        return isPage;
    }
}
