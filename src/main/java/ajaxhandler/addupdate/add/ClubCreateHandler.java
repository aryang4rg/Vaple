package ajaxhandler.addupdate.add;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.Activity;
import databaseobject.Club;
import databaseobject.User;
import main.MainServlet;
import util.ImageUtil;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClubCreateHandler implements AjaxHandler
{
    MainServlet mainServlet = new MainServlet();

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        String name = Util.asText(request.get("name"));
        String description = Util.asText(request.get("description"));
        String type = Util.asText(request.get("type"));
        String city = Util.asText(request.get("city"));
        String state = Util.asText(request.get("state"));
        String country = Util.asText(request.get("country"));
        String picture = Util.asText(request.get("image"));

        Club club = new Club(name, description, country, state, city, type, user.getObjectID());

        if (!Util.checkIfStringsAreValid(name,description,type,city,state,country))
        {
            return 400;
        }

        if(picture != null){
            try{
                ImageUtil.writeToFile(mainServlet.getFile("/cdn/club/" + club.getObjectID().toHexString() + ".png"), picture);
            }catch(IOException e){
                response.put("image", "Error creating image");
            }

        }

        Club.databaseConnectivity().addInDatabase(club);
        return 200;
    }

    @Override
    public boolean isPage() {
        return true;
    }
}
