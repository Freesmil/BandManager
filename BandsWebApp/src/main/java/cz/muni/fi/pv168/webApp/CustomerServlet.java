package cz.muni.fi.pv168.webApp;

import cz.muni.fi.pv168.bandsproject.Customer;
import cz.muni.fi.pv168.bandsproject.CustomerManager;
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
@WebServlet(CustomerServlet.URL_MAPPING + "/*")
public class CustomerServlet extends HttpServlet {

    private static final String LIST_JSP = "/listCustomer.jsp";
    public static final String URL_MAPPING = "/customers";

    private final static Logger log = LoggerFactory.getLogger(CustomerServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showCustomerList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                String name = request.getParameter("name");
                String phoneNumber = request.getParameter("phoneNumber");
                String address = request.getParameter("address");
                if (address==null || address.length()==0
                        || phoneNumber==null || phoneNumber.length()==0
                        || name == null || name.length() == 0) {
                    request.setAttribute("chyba", "Je nutne vyplnit vsechny hodnoty!");
                    break;
                }
                try {
                    Customer customer = new Customer();
                    customer.setName(name);
                    customer.setPhoneNumber(phoneNumber);
                    customer.setAddress(address);

                    getCustomerManager().createCustomer(customer);
                    log.debug("created {}",customer);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException ex) {
                    log.error("Cannot add customer", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getCustomerManager().deleteCustomer(getCustomerManager().getCustomer(id));
                    log.debug("deleted customer {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException ex) {
                    log.error("Cannot delete customer", ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    return;
                }
            case "/edit":
                try {
                    Long eid = Long.valueOf(request.getParameter("id"));
                    request.setAttribute("editCustomer", getCustomerManager().getCustomer(eid));
                }
                catch (Exception ex) {
                    request.setAttribute("chyba", "ID of customer is not correct.");
                }
                break;
            case "/update":
                try {
                    Long id = Long.parseLong(request.getParameter("id"));
                    name = request.getParameter("name");
                    phoneNumber = request.getParameter("phoneNumber");
                    address = request.getParameter("address");
                    if (address==null || address.length()==0
                            || phoneNumber==null || phoneNumber.length()==0
                            || name == null || name.length() == 0) {
                        request.setAttribute("chyba", "Some field is not correctly filled.");
                        break;
                    }
                    try {
                        Customer customer = getCustomerManager().getCustomer(id);
                        customer.setName(name);
                        customer.setPhoneNumber(phoneNumber);
                        customer.setAddress(address);
                        getCustomerManager().updateCustomer(customer);
                        log.debug("updated {}",customer);
                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException ex) {
                        log.error("Cannot add customer", ex);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                        return;
                    }
                } catch (Exception ex) {
                    request.setAttribute("chyba", "Some field is not correctly filled.");
                }
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
                return;
        }
        showCustomerList(request, response);
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
     * Stores the list of customers to request attribute "customers" and forwards to the JSP to display it.
     */
    private void showCustomerList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("customers", getCustomerManager().getAllCustomers());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            log.error("Cannot show customer", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
