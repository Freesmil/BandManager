package cz.muni.fi.pv168.webApp;

import cz.muni.fi.pv168.bandsproject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Lenka
 */
@WebServlet(XmlServlet.URL_MAPPING + "/*")
@MultipartConfig(location = "")
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

                try
                {
                    DocumentBuilderFactory docFactoryImport = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilderImport = docFactoryImport.newDocumentBuilder();
                    Document docImport = docBuilderImport.parse(fileContent);

                    BandManager bandManager = getBandManager();
                    for (Node bandElement : new Nodes(docImport.getElementsByTagName("bands"))) {
                        Band band = new Band();

                        Node bandId = bandElement.getAttributes().getNamedItem("id");
                        if (bandId != null) {
                            band.setId(Long.valueOf(bandId.getTextContent()));
                        }

                        for (Node bandChildElement : new Nodes(bandElement.getChildNodes())) {
                            switch (bandChildElement.getNodeName()) {
                                case "name":
                                    band.setBandName(bandChildElement.getTextContent());
                                    break;
                                case "styles":
                                    List<Style> styles = new ArrayList<>();
                                    for (Node styleElement : new Nodes(bandChildElement.getChildNodes())) {
                                        styles.add(Style.valueOf(styleElement.getTextContent()));
                                    }
                                    band.setStyles(styles);
                                    break;
                                case "region":
                                    band.setRegion(Region.valueOf(bandChildElement.getTextContent()));
                                    break;
                                case "pricePerHour":
                                    band.setPricePerHour(Double.valueOf(bandChildElement.getTextContent()));
                                    break;
                                case "rate":
                                    band.setRate(Double.valueOf(bandChildElement.getTextContent()));
                                    break;
                            }
                        }

                        bandManager.createBand(band);
                    }

                    CustomerManager customerManager = getCustomerManager();
                    for (Node customerElement : new Nodes(docImport.getElementsByTagName("customers"))) {
                        Customer customer = new Customer();

                        Node bandId = customerElement.getAttributes().getNamedItem("id");
                        if (bandId != null) {
                            customer.setId(Long.valueOf(bandId.getTextContent()));
                        }

                        for (Node customerChildElement : new Nodes(customerElement.getChildNodes())) {
                            switch (customerChildElement.getNodeName()) {
                                case "name":
                                    customer.setName(customerChildElement.getTextContent());
                                    break;
                                case "phoneNumber":
                                    customer.setPhoneNumber(customerChildElement.getTextContent());
                                    break;
                                case "address":
                                    customer.setAddress(customerChildElement.getTextContent());
                                    break;
                            }
                        }

                        customerManager.createCustomer(customer);
                    }

                    LeaseManager leaseManager = getLeaseManager();
                    for (Node leaseElement : new Nodes(docImport.getElementsByTagName("leases"))) {
                        Lease lease = new Lease();

                        Node bandId = leaseElement.getAttributes().getNamedItem("id");
                        if (bandId != null) {
                            lease.setId(Long.valueOf(bandId.getTextContent()));
                        }

                        for (Node leaseChildElement : new Nodes(leaseElement.getChildNodes())) {
                            switch (leaseChildElement.getNodeName()) {
                                case "customerId":
                                    lease.setCustomer(getCustomerManager().getCustomer(Long.valueOf(leaseChildElement.getTextContent())));
                                    break;
                                case "bandId":
                                    lease.setBand(getBandManager().findBandById(Long.valueOf(leaseChildElement.getTextContent())));
                                    break;
                                case "date":
                                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    lease.setDate(formatter.parse(leaseChildElement.getTextContent()));
                                    break;
                                case "place":
                                    lease.setPlace(Region.valueOf(leaseChildElement.getTextContent()));
                                    break;
                                case "duration":
                                    lease.setDuration(Integer.valueOf(leaseChildElement.getTextContent()));
                                    break;
                            }
                        }

                        leaseManager.createLease(lease);
                    }
                }
                catch(Exception e)
                {
                    log.error("Cannot parse xml file for import: " + e.getMessage());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot import xml");
                }



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

                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                    Document doc = docBuilder.newDocument();
                    Element rootElement = doc.createElement("data");
                    doc.appendChild(rootElement);

                    if (bands) {
                        Element bandsElement = doc.createElement("bands");
                        for (Band band : getBandManager().getAllBands()) {
                            Element bandElement = doc.createElement("band");
                            bandElement.setAttribute("id", band.getId().toString());
                            bandsElement.appendChild(bandElement);

                            Element name = doc.createElement("name");
                            name.appendChild(doc.createTextNode(band.getName()));
                            bandElement.appendChild(name);

                            Element styles = doc.createElement("styles");
                            for (Style style : band.getStyles()) {
                                Element styleElement = doc.createElement("style");
                                styleElement.appendChild(doc.createTextNode(style.name()));
                                styles.appendChild(styleElement);
                            }
                            bandElement.appendChild(styles);

                            Element region = doc.createElement("region");
                            region.appendChild(doc.createTextNode(band.getRegion().name()));
                            bandElement.appendChild(region);

                            Element price = doc.createElement("pricePerHour");
                            price.appendChild(doc.createTextNode(band.getPricePerHour().toString()));
                            bandElement.appendChild(price);

                            Element rate = doc.createElement("rate");
                            rate.appendChild(doc.createTextNode(band.getRate().toString()));
                            bandElement.appendChild(rate);
                        }
                        rootElement.appendChild(bandsElement);
                    }

                    if (customers) {
                        Element customersElement = doc.createElement("customers");
                        for (Customer customer : getCustomerManager().getAllCustomers()) {
                            Element customerElement = doc.createElement("customer");
                            customerElement.setAttribute("id", customer.getId().toString());
                            customersElement.appendChild(customerElement);

                            Element name = doc.createElement("name");
                            name.appendChild(doc.createTextNode(customer.getName()));
                            customerElement.appendChild(name);

                            Element phone = doc.createElement("phoneNumber");
                            phone.appendChild(doc.createTextNode(customer.getPhoneNumber()));
                            customerElement.appendChild(phone);

                            Element address = doc.createElement("address");
                            address.appendChild(doc.createTextNode(customer.getAddress()));
                            customerElement.appendChild(address);
                        }
                        rootElement.appendChild(customersElement);
                    }

                    if (leases) {
                        Element leasesElement = doc.createElement("leases");
                        for (Lease lease : getLeaseManager().findAllLeases()) {
                            Element leaseElement = doc.createElement("lease");
                            leaseElement.setAttribute("id", lease.getId().toString());
                            leasesElement.appendChild(leaseElement);

                            Element customerId = doc.createElement("customerId");
                            customerId.appendChild(doc.createTextNode(lease.getCustomer().getId().toString()));
                            leaseElement.appendChild(customerId);

                            Element bandId = doc.createElement("bandId");
                            bandId.appendChild(doc.createTextNode(lease.getBand().getId().toString()));
                            leaseElement.appendChild(bandId);

                            Element date = doc.createElement("date");
                            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            date.appendChild(doc.createTextNode(formatter.format(lease.getDate())));
                            leaseElement.appendChild(date);

                            Element place = doc.createElement("place");
                            place.appendChild(doc.createTextNode(lease.getPlace().name()));
                            leaseElement.appendChild(place);

                            Element duration = doc.createElement("duration");
                            duration.appendChild(doc.createTextNode(Integer.toString(lease.getDuration())));
                            leaseElement.appendChild(duration);
                        }
                        rootElement.appendChild(leasesElement);
                    }

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);

                    StreamResult result = new StreamResult(writer);
                    transformer.transform(source, result);

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

    class Nodes extends HashSet<Node> {
        Nodes(NodeList list) {
            if (list != null) {
                for (int i = 0; i < list.getLength(); i++) {
                    add(list.item(i));
                }
            }
        }
    }
}

