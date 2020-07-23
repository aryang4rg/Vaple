import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

public interface AjaxHandler
{
	int service(HttpServletRequest req, HttpServletResponse resp, JsonNode request, ObjectNode response, String[] uriSplit, User user) throws ServletException, IOException;
	boolean isPage();
}
