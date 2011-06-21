package net.metadata.dataspace.servlets;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: alabri
 * Date: 21/06/11
 * Time: 11:50 AM
 */
public class ATOMDocServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(getClass());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String file = getServletContext().getRealPath("/doc/");
        File f = new File(file);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".html");
            }
        };
        String[] fileNames = f.list(filter);
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date oldDate = new Date(0);
        Date newDate = new Date(0);
        String latestVersion = "";
        for (String fileName : fileNames) {
            String dateString = fileName.split("-")[1].split(".htm")[0];
            try {
                newDate = formatter.parse(dateString);
            } catch (ParseException e) {
                logger.fatal("Could not parse date in html file " + fileName);
            }
            if (newDate.after(oldDate)) {
                oldDate = newDate;
                latestVersion = "atom-" + dateString + ".html";
            }
        }
        response.setContentType("text/html;charset=utf-8");
        InputStream is = getServletContext().getResourceAsStream("/doc/" + latestVersion);
        if (is != null) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            PrintWriter writer = response.getWriter();
            String text = "";
            while ((text = reader.readLine()) != null) {
                writer.println(text);
            }
        }
    }
}
