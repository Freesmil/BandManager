package cz.muni.fi.pv168.webApp;

import cz.muni.fi.pv168.bandsproject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        request.setCharacterEncoding("utf-8");

        String action = request.getPathInfo();
        switch (action) {
            case "/filter":
                try {
                    String name = request.getParameter("name");
                    String[] stylesTexts = request.getParameterValues("styles");
                    String region = request.getParameter("region");
                    String pricePerHourFrom = request.getParameter("pricePerHourFrom");
                    String pricePerHourTo = request.getParameter("pricePerHourTo");
                    String rate = request.getParameter("rate");

                    List<Style> styles = new ArrayList<>();
                    if (stylesTexts != null) {
                        for (String style : stylesTexts) {
                            styles.add(Style.valueOf(style));
                        }
                    }

                    Collection<Band> collection = null;
                    if (name != null && name.length() > 0) {
                        collection = getBandManager().findBandByName(name);
                    }

                    if (styles.size() != 0) {
                        Collection<Band> filter = getBandManager().findBandByStyles(styles);
                        if (collection == null) collection = filter;
                        else {
                            collection = intersection(collection, filter);
                        }
                    }

                    if (region != null && !region.equals("-")) {
                        List<Region> list = new ArrayList<>();
                        list.add(Region.valueOf(region));
                        Collection<Band> filter = getBandManager().findBandByRegion(list);
                        if (collection == null) collection = filter;
                        else {
                            collection = intersection(collection, filter);
                        }
                    }

                    if (pricePerHourFrom != null && pricePerHourFrom.length() > 0 && pricePerHourTo != null && pricePerHourTo.length() > 0) {
                        Collection<Band> filter = getBandManager().findBandByPriceRange(Double.valueOf(pricePerHourFrom),Double.valueOf(pricePerHourTo));
                        if (collection == null) collection = filter;
                        else {
                            collection = intersection(collection, filter);
                        }
                    }

                    if (rate != null && rate.length() > 0) {
                        Collection<Band> filter = getBandManager().findBandByRate(Double.valueOf(rate));
                        if (collection == null) collection = filter;
                        else {
                            collection = intersection(collection, filter);
                        }
                    }

                    request.setAttribute("bands", collection);
                    request.setAttribute("styles", Style.values());
                    request.setAttribute("regions", Region.values());
                    request.getRequestDispatcher(LIST_JSP).forward(request, response);
                } catch (Exception e) {
                    log.error("Cannot filter bands", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                return;
            case "/add":
                try {
                    String name = request.getParameter("name");
                    String[] stylesTexts = request.getParameterValues("styles");
                    Region region = Region.valueOf(request.getParameter("region"));
                    Double pricePerHour = Double.parseDouble(request.getParameter("pricePerHour"));
                    Double rate = Double.parseDouble(request.getParameter("rate"));

                    List<Style> styles = new ArrayList<>();
                    for (String style : stylesTexts) {
                        styles.add(Style.valueOf(style));
                    }

                    if (name == null
                            || name.length() == 0
                            || styles.size() == 0
                            || region == null
                            || pricePerHour < 0
                            || rate < 0) {
                        throw new Exception("Some field is not correctly filled.");
                    }

                    try {

                        Band band = new Band();
                        band.setBandName(name);
                        band.setStyles(styles);
                        band.setRegion(region);
                        band.setPricePerHour(pricePerHour);
                        band.setRate(rate);
                        getBandManager().createBand(band);
                        log.debug("created {}",band);

                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException e) {
                        log.error("Cannot add band", e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                }
                catch(Exception ex) {
                    request.setAttribute("chyba", "Je nutné vyplniť všetky hodnoty správne!");
                    break;
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
            case "/edit":
                try {
                    Long eid = Long.valueOf(request.getParameter("id"));
                    request.setAttribute("editBand", getBandManager().findBandById(eid));
                }
                catch (Exception ex) {
                    request.setAttribute("chyba", "ID kapely nie je platné!");
                }
                break;
            case "/update":
                try {
                    String uname = request.getParameter("name");
                    String[] ustylesTexts = request.getParameterValues("styles");
                    Region uregion = Region.valueOf(request.getParameter("region"));
                    Double upricePerHour = Double.parseDouble(request.getParameter("pricePerHour"));
                    Double urate = Double.parseDouble(request.getParameter("rate"));
                    long id = Long.parseLong(request.getParameter("id"));

                    List<Style> ustyles = new ArrayList<>();
                    for (String ustyle : ustylesTexts) {
                        ustyles.add(Style.valueOf(ustyle));
                    }
                    if (uname == null
                            || uname.length() == 0
                            || ustyles.size() == 0
                            || uregion == null
                            || upricePerHour < 0
                            || urate < 0) {
                        throw new Exception("Some field is not correctly filled.");
                    }

                    try {
                        Band band = getBandManager().findBandById(id);
                        band.setBandName(uname);
                        band.setStyles(ustyles);
                        band.setRegion(uregion);
                        band.setPricePerHour(upricePerHour);
                        band.setRate(urate);
                        getBandManager().updateBand(band);
                        log.debug("updated {}",band);

                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException e) {
                        log.error("Cannot update band", e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                }
                catch(Exception ex) {
                    request.setAttribute("chyba", "Je nutné vyplniť všetky hodnoty správne!");
                    break;
                }
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
                return;
        }
        showBandsList(request, response);
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
            request.setAttribute("styles", Style.values());
            request.setAttribute("regions", Region.values());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            log.error("Cannot show bands", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Collection<Band> intersection(Collection<Band> list1, Collection<Band> list2) {
        Collection<Band> list = new ArrayList<Band>();

        for (Band t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}


    