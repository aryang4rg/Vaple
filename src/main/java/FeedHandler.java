import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FeedHandler implements AjaxHandler
{
    public boolean isPage(){
        return true;
    }

    private static FeedHandler feedHandler = new FeedHandler();
    public static FeedHandler getInstance()
    {
        return feedHandler;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {
        response.put("type", "feed");

        ObjectNode node = Util.createObjectNode();

        response.put("data", node);

        return 200;
    }
}
