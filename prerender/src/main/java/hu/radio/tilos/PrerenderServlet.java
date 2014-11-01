package hu.radio.tilos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;

public class PrerenderServlet extends HttpServlet {

    private String workDir = "/tmp/prerender";

    private String server = "http://tilos.hu/";
    //private String server = "http://localhost/";

    private static Logger LOG = LoggerFactory.getLogger(PrerenderServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        checkWorkDir();

        LOG.debug("Prerendering | " + req.getRequestURI() + " | " + req.getHeader("User-Agent"));
        String command = workDir + "/node_modules/phantomjs/bin/phantomjs " + workDir + "/prerender.js " + server + req.getRequestURI();
        resp.setCharacterEncoding("UTF-8");
        resp.addHeader("Content-Type", "text/html; charset=utf-8");


        String html = execute(command);
        if (html == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
            html = html.replaceAll("href=\"/\"", "href=\"" + server + "\"");
            html = html.replaceAll("<script.*/script>", "");
            resp.getWriter().write(html);
            resp.setStatus(HttpServletResponse.SC_OK);
        }

    }

    private String execute(String command) {
        try {
            String line;
            Process p = Runtime.getRuntime().exec(command, new String[0], new File(workDir));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("UTF-8")));
            StringBuilder b = new StringBuilder();
            while ((line = input.readLine()) != null) {
                b.append(line);
                b.append("\n");
            }
            input.close();
            return b.toString();
        } catch (Exception err) {
            LOG.error("Error on prerendering with command: " + command, err);
            return null;
        }
    }

    private void checkWorkDir() {
        File f = new File(workDir);
        if (!f.exists()) {
            f.mkdirs();
        }
        extract("package.json", f);
        extract("prerender.js", f);
        if (!new File(f, "node_modules").exists()) {
            execute("npm update");
        }
    }

    private void extract(String resourceName, File f) {
        try (OutputStream out = new FileOutputStream(new File(f, resourceName))) {
            try (InputStream stream = getClass().getResourceAsStream("/" + resourceName)) {
                byte[] buffer = new byte[1240];
                int s = 0;
                while ((s = stream.read(buffer)) > 0) {
                    out.write(buffer, 0, s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
