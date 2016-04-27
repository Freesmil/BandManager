package cz.muni.fi.pv168.webApp;

import cz.muni.fi.pv168.bandsproject.Band;
import cz.muni.fi.pv168.bandsproject.BandManager;
import cz.muni.fi.pv168.bandsproject.Region;
import cz.muni.fi.pv168.bandsproject.ServiceFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 *
 * @author Lenka
 */
@WebServlet(BandsServlet.URL_MAPPING + "/*")
public class BandsServlet extends HttpServlet {
    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/bands";

    private final static Logger log = LoggerFactory.getLogger(BandsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showBandsList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //aby fungovala ?estina z formul�?e
        request.setCharacterEncoding("utf-8");
        //akce podle p?�pony v URL
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                //na?ten� POST parametr? z formul�?e
                String name = request.getParameter("name");
                String styles = request.getParameter("styles"); //prerobit na list??
                Region region = Region.valueOf(request.getParameter("region"));
                Double pricePerHour = Double.parseDouble(request.getParameter("pricePerHour"));
                Double rate = Double.parseDouble(request.getParameter("rate"));

                //kontrola vypln?n� hodnot
                if (name == null
                        || name.length() == 0
                        || styles == null
                        || styles.length() == 0
                        || region == null
                        || pricePerHour < 0
                        || rate < 0) {
                    request.setAttribute("chyba", "Je nutn� vyplnit v�echny hodnoty spravne!");
                    showBandsList(request, response);
                    return;
                }
                //zpracov�n� dat - vytvo?en� z�znamu v datab�zi
                try {
                    Band band = new Band();
                    band.setBandName(name);
                    band.setStyles(null); //styly musia byt v liste
                    band.setRegion(region);
                    band.setPricePerHour(pricePerHour);
                    band.setRate(rate);
                    getBandManager().createBand(band);
                    log.debug("created {}",band);
                    //redirect-after-POST je ochrana p?ed v�cen�sobn�m odesl�n�m formul�?e
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    log.error("Cannot add band", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getBandManager().deleteBand(getBandManager().findBandById(id));
                    log.debug("deleted band {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    log.error("Cannot delete band", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                //na?ten� POST parametr? z formul�?e
                String uname = request.getParameter("name");
                String ustyles = request.getParameter("styles"); //prerobit na list??
                Region uregion = Region.valueOf(request.getParameter("region"));
                Double upricePerHour = Double.parseDouble(request.getParameter("pricePerHour"));
                Double urate = Double.parseDouble(request.getParameter("rate"));
                long id = Long.parseLong(request.getParameter("id"));

                //kontrola vypln?n� hodnot
                if (uname == null
                        || uname.length() == 0
                        || ustyles == null
                        || ustyles.length() == 0
                        || uregion == null
                        || upricePerHour < 0
                        || urate < 0
                        || id <= 0) {
                    request.setAttribute("chyba", "Je nutn� vyplnit v�echny hodnoty spravne!");
                    showBandsList(request, response);
                    return;
                }
                //zpracov�n� dat - vytvo?en� z�znamu v datab�zi
                try {
                    Band band = new Band();
                    band.setBandName(uname);
                    band.setStyles(null); //styly musia byt v liste
                    band.setRegion(uregion);
                    band.setPricePerHour(upricePerHour);
                    band.setRate(urate);
                    getBandManager().updateBand(band);
                    log.debug("created {}",band);
                    //redirect-after-POST je ochrana p?ed v�cen�sobn�m odesl�n�m formul�?e
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    log.error("Cannot add band", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
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
     * Stores the list of bands to request attribute "bands" and forwards to the JSP to display it.
     */
    private void showBandsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("bands", getBandManager().getAllBands());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            log.error("Cannot show bands", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}


    