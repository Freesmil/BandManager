package cz.muni.fi.pv168.bandsproject;

import org.exist.xmldb.XQueryService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import sun.tools.jar.Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import org.xmldb.api.base.Collection;

/**
 * Created by Lenka on 30.4.2016.
 */
public class DBUtils {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static java.util.Collection<Customer> selectCustomersFromDBWhere(Collection collection, String condition, String[] arguments) throws XMLDBException {
        List<Customer> resultList = new ArrayList<>();
        String xQuery;

        if(condition != null && !condition.isEmpty()) {
            xQuery = "let $doc := doc($document) " +
                    "return $doc/customers/customer[" + condition + "]";
        }else{
            xQuery = "let $doc := doc($document) " +
                    "return $doc/customers/customer";
        }

        XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

        service.declareVariable("document", "/db/bands/customers.xml");

        if(arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                service.declareVariable("argument" + i, arguments[i]);
            }
        }

        service.setProperty("indent", "yes");
        CompiledExpression compiled = service.compile(xQuery);

        ResourceSet res = service.execute(compiled);
        ResourceIterator it = res.getIterator();
        while(it.hasMoreResources()){
            Resource resource = it.nextResource();
            resultList.add(parseCustomerFromXML(resource.getContent().toString()));
        }
        return resultList;
    }

    public static java.util.Collection<Customer> selectCustomersFromDBWhere(Collection collection) throws XMLDBException{
        return selectCustomersFromDBWhere(collection, null, null);
    }

    public static java.util.Collection<Customer> selectCustomersFromDBWhere(Collection collection, String condition) throws XMLDBException{
        return selectCustomersFromDBWhere(collection, condition, null);
    }

    public static void bindCustomerToXQuery(Customer customer, XQueryService service){
        try {
            service.declareVariable("id", customer.getId());
            service.declareVariable("phoneNumber", customer.getPhoneNumber());
            service.declareVariable("address", customer.getAddress());
            service.declareVariable("name", customer.getName());
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException in DBUtils:"+ex);
            throw new DBException("Error while binding customer.", ex);

        }
    }

    public static Long getNextId(org.xmldb.api.base.Collection collection){
        try {
            String xQuery = "let $doc := doc($document)" +
                    "return $doc//customer-next-id/text()";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/bands/data.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            ResourceSet resultSet = service.execute(compiled);
            ResourceIterator results = resultSet.getIterator();
            if(results.hasMoreResources()) {
                Long id = Long.parseLong(results.nextResource().getContent().toString());
                if(results.hasMoreResources()){
                    log.log(Level.SEVERE, "DBException in DBUtils:data.xml has more customer-next-id element");
                    throw new DBException("data.xml has more customer-next-id element");
                }
                return id;
            }else{
                log.log(Level.SEVERE, "DBException in DBUtils: Next id does not exist");
                throw new DBException("Next id does not exist");
            }
        }catch (XMLDBException ex) {
            log.log(Level.SEVERE, "XMLDBException in DBUtils:" + ex);
            throw new DBException("Error while getting next id", ex);
        }catch (NumberFormatException ex){
            log.log(Level.SEVERE, "NumberFormatException in DBUtils:"+ex);
            throw new DBException("Error while parsing next id", ex);
        }
    }

    public static void incrementId(org.xmldb.api.base.Collection collection, Long id) {
        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update value $doc//customer-next-id with $nextId";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/bands/data.xml");
            service.declareVariable("nextId",++id);
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
        }catch (XMLDBException ex) {
            log.log(Level.SEVERE, "XMLDBException in DBUtils:"+ex);
            throw new DBException("Error while incrementing", ex);
        }catch (NumberFormatException ex){
            log.log(Level.SEVERE, "NumberFormatException in DBUtils:"+ex);
            throw new DBException("Error while parsing next id", ex);
        }
    }

    public static Customer parseCustomerFromXML(String xml){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {
                Customer customer = new Customer();
                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("customer");
                Element parent = (Element) a.item(0);
                customer.setId(Long.parseLong(parent.getAttribute("id")));

                a = parent.getElementsByTagName("name");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "CustomerException in DBUtils: Error while parsing name");
                    throw new CustomerException("Error while parsing name");
                }
                Element el = (Element) a.item(0);
                customer.setName(el.getTextContent());

                a = parent.getElementsByTagName("phoneNumber");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "CustomerException in DBUtils: Error while pustomersing phoneNumber");
                    throw new CustomerException("Error while pustomersing phoneNumber");
                }
                el = (Element) a.item(0);
                customer.setPhoneNumber(el.getTextContent());

                a = parent.getElementsByTagName("address");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "CustomerException in DBUtils: Error while parsing address");
                    throw new CustomerException("Error while parsing address");
                }
                el = (Element) a.item(0);
                customer.setAddress(el.getTextContent());

                return customer;

            } catch (SAXException e) {
                log.log(Level.SEVERE, "SAXException in DBUtils:"+e);
                throw new CustomerException("Error creating document from xml for parsing");
            } catch (IOException e) {
                log.log(Level.SEVERE, "IOException in DBUtils:"+e);
                throw new CustomerException("Error parsing customer");
            }
        } catch (ParserConfigurationException ex) {
            log.log(Level.SEVERE, "ParseFormatException in DBUtils:"+ex);
            throw new CustomerException("Error while configure pustomerser", ex);
        }
    }

    public static void createIfNotExistsCustomerResource() throws IllegalAccessException, InstantiationException, ClassNotFoundException, XMLDBException {
        Properties configProperty = new ConfigProperty();
        Collection collection = loadOrCreateDatabaseCollection();

        XMLResource resource = (XMLResource)collection.getResource(configProperty.getProperty("db_bandResourceName"));
        if (resource == null) {
            resource = (XMLResource) collection.createResource(configProperty.getProperty("db_customerResourceName"), "XMLResource");
            resource.setContent("<customers></customers>");
            collection.storeResource(resource);

            resource = (XMLResource) collection.createResource(configProperty.getProperty("db_metaData"), "XMLResource");
            resource.setContent("<data><customer-next-id>1</customer-next-id></data>");
            collection.storeResource(resource);
        }
    }

    public static Collection loadOrCreateDatabaseCollection() throws IllegalAccessException, InstantiationException, ClassNotFoundException, XMLDBException {
        Properties configProperty = new ConfigProperty();

        Class c = Class.forName(configProperty.getProperty("db_driver"));
        Database database = (Database) c.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        Collection collection = DatabaseManager.getCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"), configProperty.getProperty("db_name"), configProperty.getProperty("db_password"));

        if (collection == null) {
            org.xmldb.api.base.Collection parent = DatabaseManager.getCollection(configProperty.getProperty("db_prefix"), configProperty.getProperty("db_name"), configProperty.getProperty("db_password"));
            CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
            mgt.createCollection(configProperty.getProperty("db_collection"));
            parent.close();
            collection = DatabaseManager.getCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"), configProperty.getProperty("db_name"), configProperty.getProperty("db_password"));
        }

        return collection;
    }

    public static void dropDatabaseCollection() throws IllegalAccessException, InstantiationException, ClassNotFoundException, XMLDBException {
        Properties configProperty = new ConfigProperty();

        Class c = Class.forName(configProperty.getProperty("db_driver"));
        Database database = (Database) c.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        org.xmldb.api.base.Collection parent = DatabaseManager.getCollection(configProperty.getProperty("db_prefix"),configProperty.getProperty("db_name"),configProperty.getProperty("db_password"));
        CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");

        mgt.removeCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"));
        parent.close();
    }
}
