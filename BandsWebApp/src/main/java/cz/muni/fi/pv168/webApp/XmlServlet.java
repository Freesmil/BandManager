package cz.muni.fi.pv168.webApp;

import cz.muni.fi.pv168.bandsproject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenka
 */
@WebServlet(XmlServlet.URL_MAPPING + "/*")
public class XmlServlet extends HttpServlet {

    private static final String LIST_JSP = "/xml.jsp";
    public static final String URL_MAPPING = "/xml";

    private final static Logger log = LoggerFactory.getLogger(XmlServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(LIST_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        switch (action) {
            case "/import":
                Part filePart = request.getPart("file");
                InputStream fileContent = filePart.getInputStream();

                //TODO

                request.getRequestDispatcher(LIST_JSP).forward(request, response);
            case "/export":
                boolean bands = false;
                boolean leases = false;
                boolean customers = false;

                String[] dataTexts = request.getParameterValues("data");

                if (dataTexts != null) {
                    for (String dataText : dataTexts) {
                        if (dataText.equals("Bands")) bands = true;
                        if (dataText.equals("Leases")) leases = true;
                        if (dataText.equals("Customers")) customers = true;
                    }
                }

                response.setContentType("text/xml");
                response.setHeader("Content-Disposition", "attachment; filename=\"export.xml\"");
                try
                {
                    OutputStream outputStream = response.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

                    //TODO

                    writer.flush();
                    writer.close();
                }
                catch(Exception e)
                {
                    log.error("Cannot generate xml file for export: " + e.getMessage());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot generate xml");
                }
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    /**
     * Gets CustomerManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return CustomerManager instance
     */
    private CustomerManager getCustomerManager() {
        return (CustomerManager) getServletContext().getAttribute("customerManager");
    }

    /**
     * Gets BandManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return BandManager instance
     */
    private BandManager getBandManager() {
        return (BandManager) getServletContext().getAttribute("bandManager");
    }

    /**
     * Gets LeaseManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return LeaseManager instance
     */
    private LeaseManager getLeaseManager() {
        return (LeaseManager) getServletContext().getAttribute("leaseManager");
    }


}
