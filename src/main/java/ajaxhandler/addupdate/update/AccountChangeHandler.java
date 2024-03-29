package ajaxhandler.addupdate.update;

import ajaxhandler.AjaxHandler;
import ajaxhandler.addupdate.add.SignUpServlet;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import databaseobject.*;
import main.MainServlet;
import util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountChangeHandler implements AjaxHandler
{
    public boolean isPage(){
        return false;
    }

    @Override
    public int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User u) throws ServletException, IOException {
		if(uriSplit.length > 0)
			return 404;
		if(u == null)
			return 400;

		String email = Util.asText(request.get("email"));
		String name = Util.asText(request.get("name"));
		String password = Util.asText(request.get("password"));
		String old_password = Util.asText(request.get("password"));
		String location_country = Util.asText(request.get("location_country"));
		String location_state = Util.asText(request.get("location_state"));
		String location_city = Util.asText(request.get("location_city"));
		String description = Util.asText(request.get("description"));
		String picture = Util.asText(request.get("image"));

		String error = null;

		do{
			if(email != null){
				email = Util.trimAndnullIfSpecialCharacters(email);

				if(email == null || !SignUpServlet.isValidEmail(email)){
					error = "Invalid Email";

					break;
				}
			}

			if(password != null){
				old_password = Util.trimAndnullIfSpecialCharacters(old_password);

				if(old_password == null){
					error = "Incorrect password";

					break;
				}

				old_password = PasswordHasher.getInstance().createHash(old_password);

				if(!u.getPassword().equals(old_password)){
					error = "Incorrect password";

					break;
				}

				password = Util.trimString(password);

				if(password == null){
					error = "Invalid password";

					break;
				}

				password = Util.nullIfSpecialCharacters(password);

				if(password == null){
					error = "Special characters not allowed in password";

					break;
				}

				if(password.length() > 32){
					error = "Password too long";

					break;
				}

				password = PasswordHasher.getInstance().createHash(password);
			}

			if(name != null){
				name = Util.removeTrimAndNonAlphanumeric(name);

				if(name == null){
					error = "Invalid name";

					break;
				}
			}

			if(location_country != null){
				location_country = Util.removeTrimAndNonAlphanumeric(location_country);

				if(location_country == null){
					error = "Invalid country";

					break;
				}
			}

			if(location_state != null){
				location_state = Util.removeTrimAndNonAlphanumeric(location_state);

				if(location_state == null){
					error = "Invalid state or province";

					break;
				}
			}

			if(location_city != null){
				location_city = Util.removeTrimAndNonAlphanumeric(location_city);

				if(location_city == null){
					error = "Invalid city";

					break;
				}
			}

			if(description != null){
				description = description.trim();

				if(description.length() == 0)
					break;
				description = Util.nullIfSpecialCharacters(description);

				if(description == null){
					error = "Special characters not allowed in description";

					break;
				}

				if(description.length() > 256)
					description = description.substring(0, 256);
			}
		}while(false);

		if(error != null){
			response.put("error", error);

			return 200;
		}

		if(email != null)
			if(u.getEmail().equals(email)){
				response.put("error", "Already your email");

				return 200;
			}

			if(User.databaseConnectivity().infoExistsInDatabase(User.EMAIL, email)){
				response.put("error", "Email already in use");

				return 200;
			}
		if(email != null)
			u.setEmail(email);
		if(password != null)
			u.setPassword(password);
		if(name != null)
			u.setName(name);
		if(location_country != null)
			u.setCountry(location_country);
		if(location_state != null)
			u.setState(location_state);
		if(location_city != null)
			u.setCity(location_city);
		if(description != null)
			u.setDescription(description);
		User.databaseConnectivity().updateInDatabase(u);

		ObjectNode node = u.toProfileNode();

		if(picture != null){
			try{
				error = ImageUtil.writeToFile(mainServlet.getFile("/cdn/profile/" + u.getObjectID().toHexString() + ".png"), picture);
			}catch(IOException e){
				node.put("image", "Error creating image");
				e.printStackTrace();
			}

			if(error != null)
				node.put("image", error);
		}

		response.put("profile", node);

        return 200;
    }

    private MainServlet mainServlet;
    AccountChangeHandler(MainServlet mainServlet)
	{
		this.mainServlet = mainServlet;
	}

    private static AccountChangeHandler instance;
    public static AccountChangeHandler getInstance(MainServlet mainServlet)
	{
		if (instance == null)
		{
			instance = new AccountChangeHandler(mainServlet);
		}
		return instance;
	}

}