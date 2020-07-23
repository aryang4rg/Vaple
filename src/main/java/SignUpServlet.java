import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet implements AjaxHandler
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

	public boolean isValidEmail(String email){
		int pi = email.lastIndexOf('.');
		int ai = email.indexOf('@');

		if(ai < 1 || pi <= ai + 1 || pi + 1 >= email.length())
			return false;
		return false;
	}

	public boolean isValidPassword(String k){
		return k.length() <= 32;
	}

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonNode node = Json.parse(req.getReader());
        String email = toValidString(node.get("email").asText());
        String name = toValidString(node.get("name").asText());
        String password = toValidString(node.get("password").asText());
        String location_country = toValidString(node.get("location_country").asText());
        String location_state = toValidString(node.get("location_state").asText());
        String location_city = toValidString(node.get("location_city").asText());

        //TODO make token, verify fields exist
       // User user = new User(name,email,password, location_country, location_state, location_city, "", );

		if(email == null || name == null || password == null || location_country == null ||
			location_state == null || location_city == null){
				resp.getWriter().println("{\"token\": null}");

				return;
			}
		if(!isValidEmail(email)){
			resp.getWriter().println("{\"token\": null, \"error\": \"Invalid email\"}");

			return;
		}

		if(!isValidPassword(password)){
			resp.getWriter().println("{\"token\": null, \"error\": \"Password must be less than 32 characters\"}");

			return;
		}

		if(DatabaseConnectivity.emailAlreadyExists(email)){
			resp.getWriter().println("{\"token\": null, \"error\": \"Email already in use\"}");

			return;
		}

		/* here is where you'd encrypt the password */
    }
}
