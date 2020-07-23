import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Hashtable;

@WebServlet("/")
public class MainServlet extends HttpServlet
{
    private LoginServlet loginServlet = new LoginServlet();
    private SignUpServlet signUpServlet = new SignUpServlet();
    private Hashtable<String, AjaxHandler> pathToHandler = ajaxResponseToHandler();
    public static void sendFile(OutputStream out, File file) throws FileNotFoundException, IOException {
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[16384];
        int length;
        while ((length = in.read(buffer)) > 0){
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
    }

    public  Hashtable<String, AjaxHandler> ajaxResponseToHandler()
    {
        Hashtable<String, AjaxHandler> hashtable = new Hashtable<>();
        hashtable.put("account_login", loginServlet );
        hashtable.put("account_create", signUpServlet);
        return hashtable;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("x-content-options", "nosniff");
        String relativeWebPath = "/resources" + req.getRequestURI();
        String absoluteDiskPath = getServletContext().getRealPath(relativeWebPath);
        File file = new File(absoluteDiskPath);
        String[] uriSplit = req.getRequestURI().split("[/]+");

		System.out.println("ABSOLUTE_DISK_PATH: " + absoluteDiskPath);


        if(uriSplit.length > 1 && "POST".equalsIgnoreCase(req.getMethod()))
        {

            AjaxHandler handler = pathToHandler.get(uriSplit[1]);
            if (handler != null)
            {
                handler.service(req,resp);
            }
            else
            {
                resp.setStatus(404);
            }
        }
        else if (file.exists() && file.isFile())
        {
            sendFile(resp.getOutputStream(), file);
        }
        else
        {
            resp.setHeader("Content-Type", "text/html");
            sendFile(resp.getOutputStream(), new File(getServletContext().getRealPath("/resources/page.html")));
        }
    }
}

