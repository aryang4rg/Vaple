import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityCreatorHandler implements AjaxHandler
{
    private static ActivityCreatorHandler instance = new ActivityCreatorHandler();

    public static ActivityCreatorHandler getInstance() {
        return instance;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        String title = Util.asText(request.get("title"));
        String description = Util.asText(request.get("description"));
        String type = Util.asText(request.get("type"));
        String latitude = Util.asText(request.get("latitude"));
        String longitude = Util.asText(request.get("longitude"));
        String time_start = Util.asText(request.get("time_start"));
        String time_end = Util.asText(request.get("time_end"));
        String associated_club = Util.asText(request.get("associated_club"));



        if (!Util.checkIfStringsAreValid(title,description,type,time_end,time_start, longitude, latitude))
        {
            return 400;
        }

        title = Util.trimAndnullIfSpecialCharacters(title);

        Club club = null;
        if (associated_club != null) {
            club = (Club) Club.databaseConnectivity().getByInfoInDataBase(ID, associated_club);
        }

        if (user == null)
        {
            return 400;
        }

        try {
            Activity activity = new Activity(title, description,user.getObjectID(), new ArrayList<ObjectId>(), Long.parseLong(time_start),
                    Long.parseLong(time_end), Double.parseDouble(latitude), Double.parseDouble(longitude), club);
            Activity.databaseConnectivity().addInDatabase(activity);

            ((User)User.databaseConnectivity().getByInfoInDataBase(ID, user.getObjectID())).setActivities(activity.objectID, true);
            return 200;
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return 400;
        }

    }

    @Override
    public boolean isPage() {
        return false;
    }
}
