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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Lenka
 */
@WebServlet(LeaseServlet.URL_MAPPING + "/*")
public class LeaseServlet extends HttpServlet {
    private static final String LIST_JSP = "/listLease.jsp";
    public static final String URL_MAPPING = "/leases";

    private final static Logger log = LoggerFactory.getLogger(LeaseServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showLeasesList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String action = request.getPathInfo();
        switch (action) {
            case "/filter":
                try {
                    String bandId = request.getParameter("bandId");
                    String customerId = request.getParameter("customerId");

                    Collection<Lease> collection = null;
                    if (bandId != null && bandId.length() > 0) {
                        collection = getLeasesManager().findLeasesForBand(getBandsManager().findBandById(Long.valueOf(bandId)));
                    }

                    if (customerId != null && customerId.length() > 0) {
                        Collection<Lease> filter = getLeasesManager().findLeasesForCustomer(getCustomersManager().getCustomer(Long.valueOf(customerId)));
                        if (collection == null) collection = filter;
                        else {
                            collection = intersection(collection, filter);
                        }
                    }

                    request.setAttribute("leases", collection);
                    request.setAttribute("bands", getBandsManager().getAllBands());
                    request.setAttribute("customers", getCustomersManager().getAllCustomers());
                    request.setAttribute("regions", Region.values());
                    request.getRequestDispatcher(LIST_JSP).forward(request, response);
                } catch (Exception e) {
                    log.error("Cannot filter bands", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }
                return;
            case "/add":
                try {
                    String bandId = request.getParameter("bandId");
                    String customerId = request.getParameter("customerId");
                    Date date = formatter.parse(request.getParameter("date"));
                    Region place = Region.valueOf(request.getParameter("place"));
                    Integer duration = Integer.parseInt(request.getParameter("duration"));

                    if (bandId == null
                            || bandId.length() == 0
                            || customerId == null
                            || customerId.length() == 0
                            || date == null
                            || duration < 0 ) {
                        throw new Exception("Some field is not correctly filled.");
                    }

                    try {

                        Lease lease = new Lease();
                        lease.setBand(getBandsManager().findBandById(Long.valueOf(bandId)));
                        lease.setCustomer(getCustomersManager().getCustomer(Long.valueOf(customerId)));
                        lease.setDate(date);
                        lease.setDuration(duration);
                        lease.setPlace(place);
                        getLeasesManager().createLease(lease);
                        log.debug("created {}",lease);

                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException e) {
                        log.error("Cannot add band", e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                }
                catch(Exception ex) {
                    request.setAttribute("chyba", "Some field is not correctly filled.");
                    break;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getLeasesManager().deleteLease(getLeasesManager().findLeaseById(id));
                    log.debug("deleted lease {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    log.error("Cannot delete lease", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/edit":
                try {
                    Long eid = Long.valueOf(request.getParameter("id"));
                    request.setAttribute("editLease", getLeasesManager().findLeaseById(eid));
                }
                catch (Exception ex) {
                    request.setAttribute("chyba", "ID of lease is not correct.");
                }
                break;
            case "/update":
                try {
                    String ubandId = request.getParameter("bandId");
                    String ucustomerId = request.getParameter("customerId");
                    Date udate = formatter.parse(request.getParameter("date"));
                    Region uplace = Region.valueOf(request.getParameter("place"));
                    Integer uduration = Integer.parseInt(request.getParameter("duration"));
                    long id = Long.parseLong(request.getParameter("id"));

                    if (ubandId == null
                            || ubandId.length() == 0
                            || ucustomerId == null
                            || ucustomerId.length() == 0
                            || udate == null
                            || uduration < 0 ) {
                        throw new Exception("Some field is not correctly filled.");
                    }

                    try {
                        Lease lease = getLeasesManager().findLeaseById(id);
                        lease.setCustomer(getCustomersManager().getCustomer(Long.valueOf(ucustomerId)));
                        lease.setDate(udate);
                        lease.setDuration(uduration);
                        lease.setPlace(uplace);
                        getLeasesManager().updateLease(lease);
                        log.debug("updated {}",lease);

                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException e) {
                        log.error("Cannot update band", e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                }
                catch(Exception ex) {
                    request.setAttribute("chyba", "Some field is not correctly filled.");
                    break;
                }
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
                return;
        }
        showLeasesList(request, response);
    }

    /**
     * Gets LeaseManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return LeaseManager instance
     */
    private LeaseManager getLeasesManager() {
        return (LeaseManager) getServletContext().getAttribute("leaseManager");
    }

    /**
     * Gets LeaseManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return LeaseManager instance
     */
    private BandManager getBandsManager() {
        return (BandManager) getServletContext().getAttribute("bandManager");
    }

    /**
     * Gets LeaseManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return LeaseManager instance
     */
    private CustomerManager getCustomersManager() {
        return (CustomerManager) getServletContext().getAttribute("customerManager");
    }

    /**
     * Stores the list of bands to request attribute "bands" and forwards to the JSP to display it.
     */
    private void showLeasesList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("leases", getLeasesManager().findAllLeases());
            request.setAttribute("bands", getBandsManager().getAllBands());
            request.setAttribute("customers", getCustomersManager().getAllCustomers());
            request.setAttribute("regions", Region.values());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            log.error("Cannot show leases", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Collection<Lease> intersection(Collection<Lease> list1, Collection<Lease> list2) {
        Collection<Lease> list = new ArrayList<Lease>();

        for (Lease t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}


    