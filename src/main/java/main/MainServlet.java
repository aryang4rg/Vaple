package main;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;

import ajaxhandler.*;
import ajaxhandler.addupdate.add.ActivityCreatorHandler;
import ajaxhandler.addupdate.add.ClubCreateHandler;
import ajaxhandler.addupdate.add.SignUpServlet;
import ajaxhandler.addupdate.update.AccountChangeHandler;
import ajaxhandler.addupdate.update.AddUserToActivity;
import ajaxhandler.addupdate.update.ClubFollowHandler;
import ajaxhandler.addupdate.update.FollowPersonHandler;
import ajaxhandler.fulfiller.*;
import ajaxhandler.login.LoginHandler;
import ajaxhandler.login.LoginServlet;
import ajaxhandler.login.UnverifiedHandler;
import ajaxhandler.login.VerifyAccountHandler;
import com.fasterxml.jackson.databind.JsonMappingException;
import databaseobject.*;
import util.*;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;


@WebServlet("/")
public class MainServlet extends HttpServlet
{
	public File getFile(String path){
		return new File(getServletContext().getRealPath("/resources" + path));
	}

	private static void sendFile(OutputStream out, File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[16384];
		int length;
		while ((length = in.read(buffer)) > 0){
			out.write(buffer, 0, length);
		}
		in.close();
		out.flush();
	}

	private Hashtable<String, AjaxHandler> pathToHandler = ajaxRequestToHandler();
	private Hashtable<String, String> extensionToMime = extensionToMimeHM();



	private Hashtable<String, AjaxHandler> ajaxRequestToHandler()
	{
		Hashtable<String, AjaxHandler> hashtable = new Hashtable<>();
		hashtable.put("account_login", LoginServlet.getInstance());
		hashtable.put("account_create", SignUpServlet.getInstance());
		hashtable.put("login", LoginHandler.getInstance());
		hashtable.put("profile", ProfileHandler.getInstance());
		hashtable.put("activity_create", ActivityCreatorHandler.getInstance(this));
		hashtable.put("account_change", AccountChangeHandler.getInstance(this));
		hashtable.put("join_leave_club", ClubFollowHandler.getInstance());
		hashtable.put("follow_change", FollowPersonHandler.getInstance());
		hashtable.put("activity_feed", new ActivityFeedHandler(false));
		hashtable.put("activities", new ActivityFeedHandler(false));
		hashtable.put("home", new ActivityFeedHandler(true));
		hashtable.put("new_activity", NewActivityHandler.getInstance());
		hashtable.put("verify_account", VerifyAccountHandler.getInstance());
		hashtable.put("club_create", ClubCreateHandler.getInstance(this));
		hashtable.put("add_to_activity", AddUserToActivity.getInstance());
		hashtable.put("activity", ActivityHandler.getInstance());
		hashtable.put("club", ClubHandler.getInstance());
		hashtable.put("new_club",  NewClubHandler.getInstance());
		hashtable.put("unverified_user", UnverifiedHandler.getInstance());
		hashtable.put("explore", ExploreFeedHandler.getInstance());
		hashtable.put("challenges", ChallengesHandler.getInstance());

		return hashtable;
	}

	private Hashtable<String, String> extensionToMimeHM(){
		Hashtable<String, String> map = new Hashtable<>();

		map.put("js", "application/javascript");
		map.put("css", "text/css");
		map.put("png", "image/png");
		map.put("woff2", "font/woff2");

		return map;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] uriSplit = req.getRequestURI().split("[/]+");
		User user = null;
		AjaxHandler handler = null;
		String token = req.getHeader("Authentication");

		if(token != null)
			user = (User) User.databaseConnectivity().getFromInfoInDataBase(User.TOKEN, token);


		if("POST".equalsIgnoreCase(req.getMethod()))
		{

			if(uriSplit.length == 0)
			{
				handler = pathToHandler.get("home");
				uriSplit = new String[]{};
			}
			else if(uriSplit.length >= 2){
				handler = pathToHandler.get(uriSplit[1]);
				uriSplit = Arrays.copyOfRange(uriSplit, 2, uriSplit.length);
			}

			if (handler != null)
			{
				boolean isUnverified = false;
				if (!handler.equals(VerifyAccountHandler.getInstance()) && user != null && !(Boolean)user.get(User.VERIFIED))
				{
					handler = pathToHandler.get("unverified_user");
					uriSplit = new String[]{};

				}
				if (isUnverified)
				{
					return;
				}
				JsonNode request = null;
				try {
					request = Json.parse(req.getInputStream());
				}
				catch (JsonMappingException e)
				{
					e.printStackTrace();
				}

					ObjectNode response = Util.createObjectNode();


				/* maybe remove later */

				if(user == null && handler.isPage()){
					handler = pathToHandler.get("login");
					uriSplit = new String[]{};
				}


				/* maybe remove later ^*/
				int statusCode = handler.service(req, resp, request, response, uriSplit, user);

				boolean retrieveAccount = false;

				/* on a first page load, the client will ask for account id to verify the token */

				if( request.get("retrieveAccount") != null && request.get("retrieveAccount").isBoolean() && request.get("retrieveAccount").booleanValue())
					retrieveAccount = true;
				if(handler.isPage() && retrieveAccount){
					if(user != null)
						/* found the user for that token */
						response.put("account", user.toAccountNode());
					else{
						/* that token has expired */
						ObjectNode expired = Util.createObjectNode();

						expired.put("expired", true);
						response.put("account", expired);
					}
				}

				resp.setStatus(statusCode);
				resp.setHeader("Content-Type", "text/json");
				resp.getWriter().print(Json.stringify(response));

				return;
			}
			else
			{
				resp.setStatus(404);

				return;
			}
		}
		else
		{
			File file = getFile(req.getRequestURI());

			if (file.exists() && file.isFile())
			{
				int index = file.getName().lastIndexOf('.');

				if(index != -1){
					String extension = file.getName().substring(index + 1);
					String mimeType = extensionToMime.get(extension);

					if(mimeType != null)
						resp.setHeader("Content-Type", mimeType);
				}

				resp.setHeader("x-content-options", "nosniff");

				sendFile(resp.getOutputStream(), file);

				return;
			}
			else if (uriSplit.length > 3 && (uriSplit[1].equals("cdn") && uriSplit[2].equals("profile")))
			{

				if (uriSplit[3] == null || Util.nullIfSpecialCharacters(uriSplit[3]) == null)
				{
					resp.setStatus(400);
					return;
				}
				int index = file.getName().lastIndexOf('.');
				String extension = file.getName().substring(index + 1);
				if (!extension.equals("png"))
				{
					resp.setStatus(400);
					return;
				}

				File defaultPic = getFile("/cdn/default.png");
				sendFile(resp.getOutputStream(), defaultPic);
				return;
			}
		}

		String page = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"/style/base.css\">" +
			"</head><body><script src=\"/js/base.js\"></script><script src=\"/js/loader.js\"></script>" +
			"<script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyBdBgeXjW2cX_Q_sOZvliYy-8eDQq319ss&callback=initMap\"></script>" +
			"</body></html>";
		/* send a minimized version of page.html since it never changes anyway */
		resp.setHeader("Content-Type", "text/html");
		resp.setHeader("Content-Length", "" + page.length());
		resp.getWriter().print(page);
	}
}

