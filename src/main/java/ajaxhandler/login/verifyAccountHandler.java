package ajaxhandler.login;

import ajaxhandler.AjaxHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import databaseobject.User;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class verifyAccountHandler implements AjaxHandler
{
    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException {
        if (uriSplit.length != 3 ) // 0: / 1: verify_account 2: == token
        {
            return 400;
        }
        String token = uriSplit[2];
        token = Util.trimAndnullIfSpecialCharacters(token);
        if (token== null)
        {
            return 400;
        }
        User u = (User) User.databaseConnectivity().getFromInfoInDataBase(User.VERIFICATION_TOKEN, token);
        if (u == null)
        {
            return 400;
        }
        u.setVerifiedUser(true);
        return 200;
    }

    @Override
    public boolean isPage() {
        return true;
    }
}
