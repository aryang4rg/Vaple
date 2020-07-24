package ajaxhandler.addupdate.add;

import ajaxhandler.AjaxHandler;
import databaseobject.*;
import main.MainServlet;
import util.*;

import databaseobject.Activity;
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
    private static ActivityCreatorHandler instance;
    private MainServlet mainServlet;
    ActivityCreatorHandler(MainServlet mainServlet)
    {
        this.mainServlet = mainServlet;
    }

    public static ActivityCreatorHandler getInstance(MainServlet mainServlet) {

        if (instance == null) {
            instance = new ActivityCreatorHandler(mainServlet);
        }
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
        String picture = Util.asText(request.get("image"));



        if (!Util.checkIfStringsAreValid(title,description,type,time_end,time_start, longitude, latitude, picture))
        {
            return 400;
        }

        title = Util.trimAndnullIfSpecialCharacters(title);

        Club club = null;
        if (associated_club != null) {
            club = (Club) Club.databaseConnectivity().getFromInfoInDataBase(ID, associated_club);
        }

        if (user == null)
        {
            return 400;
        }

        try {
            Activity activity = new Activity(title, description, type, user.getObjectID(), new ArrayList<ObjectId>(), Long.parseLong(time_start),
                    Long.parseLong(time_end), Double.parseDouble(latitude), Double.parseDouble(longitude), club);
            Activity.databaseConnectivity().addInDatabase(activity);

            user.setActivities(activity.getObjectID(), true);
            User.databaseConnectivity().addInDatabase(user);

            if(picture != null){
                try{
                     ImageUtil.writeToFile(mainServlet.getFile("/cdn/activity/" + activity.getObjectID().toHexString() + ".png"), picture);
                }catch(IOException e){
                    response.put("image", "Error creating image");
                }

            }

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
