import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClubFollowHandler implements AjaxHandler
{
    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        String club_id = Util.asText(request.get("club_id"));
        Boolean toJoin = request.get("toJoin").asBoolean();

        if (Util.trimAndnullIfSpecialCharacters(club_id) == null)
        {
            return 400;
        }

        Club c = (Club) Club.databaseConnectivity().getByInfoInDataBase(ID, new ObjectId(club_id));
        if (c == null)
        {
            return 400;
        }

        user.setClubs(c.getObjectID(), toJoin);
        c.setMember(user.getObjectID(), toJoin);
        return 200;
    }

    @Override
    public boolean isPage() {
        return false;
    }
}
