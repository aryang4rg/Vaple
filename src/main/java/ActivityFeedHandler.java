import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityFeedHandler implements AjaxHandler
{
    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        if (user == null)
        {
            return 400;
        }

        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<String> following = user.getFollowing();
        for (String idString : following)
        {
            User followingUser = (User)User.databaseConnectivity().getByInfoInDataBase(ID, new ObjectId(idString) );
            //followingUser.getActivities()
        }
        return 0;
    }

    @Override
    public boolean isPage() {
        return false;
    }
}
