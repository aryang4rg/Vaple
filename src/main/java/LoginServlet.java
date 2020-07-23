import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class LoginServlet implements AjaxHandler
{
	public String toValidString(String k){
		if(k == null || k.length() == 0)
			return null;
		/* removes all special characters */
		k = k.replace("[^a-zA-Z0-9_-]", "");

		if(k.length() == 0)
			return null;
		return k;
	}

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        JsonNode creds = Json.parse(req.getInputStream());
        String email = toValidString(creds.get("email").asText());
        String password = toValidString(creds.get("password").asText());
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
			ObjectNode node = JsonNodeFactory.instance.objectNode();

			node.put("account", user.toObjectNode());
			node.put("token", user.getToken());
            resp.getWriter().print(Json.stringify(node));
        }
    }
}
