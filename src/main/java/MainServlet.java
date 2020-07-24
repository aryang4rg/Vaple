import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

@WebServlet("/")
public class MainServlet extends HttpServlet
{
	private static void sendFile(OutputStream out, File file) throws FileNotFoundException, IOException {
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
		Hashtable<String, AjaxHandler> hashtable = new Hashtable<String, AjaxHandler>();
		hashtable.put("account_login", LoginServlet.getInstance());
		hashtable.put("account_create", SignUpServlet.getInstance());
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

		if("POST".equalsIgnoreCase(req.getMethod()))
		{
			AjaxHandler handler = null;

			if(uriSplit.length == 1)
				; /*handler = default handler, for home page, aka feed page */
			else if(uriSplit.length >= 2)
				handler = pathToHandler.get(uriSplit[1]);
			if (handler != null)
			{
				JsonNode request = Json.parse(req.getReader());
				ObjectNode response = Util.createObjectNode();

				User user = null;
				String token = resp.getHeader("Authentication");

				if(token != null)
					user = (User)User.databaseConnectivity().getByInfoInDataBase(User.TOKEN, token);
				int statusCode = handler.service(req, resp, request, response, Arrays.copyOfRange(uriSplit, 2, uriSplit.length), user);

				if(statusCode != 200){
					resp.setStatus(statusCode);

					return;
				}

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
			File file = new File(getServletContext().getRealPath("/resources" + req.getRequestURI()));

			if (file.exists() && file.isFile())
			{
				int index = file.getName().lastIndexOf('.');

				/* since aryan wouldn't do it */
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
		}

		String page = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"/style/base.css\">"
			+ "</head><body><script src=\"/js/base.js\"></script><script src=\"/js/loader.js\"></script></body></html>";
		/* send a minimized version of page.html since it never changes anyway */
		resp.setHeader("Content-Type", "text/html");
		resp.setHeader("Content-Length", "" + page.length());
		resp.getWriter().print(page);
	}
}

