import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet implements AjaxHandler
{
	public boolean isValid(String k){
		if(k == null || k.length() == 0)
			return false;
		return true;
	}

	public boolean isValidEmail(String email){
		int pi = email.lastIndexOf('.');
		int ai = email.indexOf('@');

		if(ai < 1 || pi <= ai + 1 || pi + 1 >= email.length())
			return false;
		//check if email doesnt already exist

		return false;
	}

	public boolean isValidPassword(String k){
		return k.length() <= 32;
	}

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonNode node = Json.parse(req.getReader());
        String email = node.get("email").asText();
        String name = node.get("name").asText();
        String password = node.get("password").asText();
        String location_country = node.get("location_country").asText();
        String location_state = node.get("location_state").asText();
        String location_city = node.get("location_city").asText();

        //TODO make token, verify fields exist
       // User user = new User(name,email,password, location_country, location_state, location_city, "", );

		if(!isValid(email) || !isValid(name) || !isValid(password) || !isValid(location_country) ||
			!isValid(location_state) || !isValid(location_city)){
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
    }
}
