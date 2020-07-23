import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet implements AjaxHandler
{
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        JsonNode creds = Json.parse(req.getReader());
        String email = creds.get("email").asText();
        String password = creds.get("password").asText();
        //TODO add encryption to password
        if (email == null || password == null)
        {
            resp.getWriter().print("{\"token\": null}\n");
        }
        User user = DatabaseConnectivity.getUserByEmail(email);
        if (user == null)
        {
            resp.getWriter().print("{\"token\": null}\n");
        }
        else if (!user.getPassword().equals(password))
        {
            resp.getWriter().print("{\"token\": null}\n");
        }
        else
        {
            JsonNode account = Json.toJson(user);
            resp.getWriter().print(Json.stringify(account));
        }
    }
}
