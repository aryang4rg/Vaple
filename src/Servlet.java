import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;

@WebServlet("/")
public class Servlet extends HttpServlet
{

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

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("x-content-options", "nosniff");
        String relativeWebPath = "/resources" + req.getRequestURI();
        String absoluteDiskPath = getServletContext().getRealPath(relativeWebPath);
        File file = new File(absoluteDiskPath);

        //System.out.println(req.getRequestURI());
        //System.out.println(req.getHeader("Accept"));

        if (("ajax").equals(req.getHeader("mode")))
        {
            resp.setStatus(200);
            resp.getWriter().println("{\"type\": \"profile\", \"data\": {}}");
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
