package ajaxhandler.fulfiller;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClubGiver implements AjaxHandler
{
    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        if (user == null)
        {
            return 400;
        }
        response.put("type", "new_activity");
        ObjectNode data = new ObjectNode(JsonNodeFactory.instance);
        response.put("data", data);
        ObjectNode clubs = new ObjectNode(JsonNodeFactory.instance);
        data.put("clubs", clubs);
        //for ()

        return 0;
    }

    @Override
    public boolean isPage() {
        return true;
    }
}
