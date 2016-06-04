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
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lenka on 6.5.2016.
 */
public class DBUtilsLease {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static java.util.Collection<Lease> selectLeasesFromDBWhere(Collection collection, String condition, String[] arguments) throws XMLDBException {
        List<Lease> resultList = new ArrayList<>();
        String xQuery;

        if(condition != null && !condition.isEmpty()) {
            xQuery = "let $doc := doc($document) " +
                    "return $doc/bands/lease[" + condition + "]";
        }else{
            xQuery = "let $doc := doc($document) " +
                    "return $doc/bands/lease";
        }

        XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

        service.declareVariable("document", "/db/bands/leases.xml");

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
            resultList.add(parseLeaseFromXML(resource.getContent().toString()));
        }
        return resultList;
    }

    public static java.util.Collection<Lease> selectLeasesFromDBWhere(Collection collection) throws XMLDBException{
        return selectLeasesFromDBWhere(collection, null, null);
    }

    public static java.util.Collection<Lease> selectLeasesFromDBWhere(Collection collection, String condition) throws XMLDBException{
        return selectLeasesFromDBWhere(collection, condition, null);
    }

    public static void bindLeaseToXQuery(Lease lease, XQueryService service){
        try {
            service.declareVariable("id", lease.getId());
            service.declareVariable("customerId", lease.getCustomer().getId().toString());
            service.declareVariable("bandId", lease.getBand().getId().toString());

            Calendar cal = Calendar.getInstance();
            cal.setTime(lease.getDate());
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            service.declareVariable("date", year + "-" + month + "-" + day);

            service.declareVariable("place", lease.getPlace().toString());
            service.declareVariable("duration", lease.getDuration());
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException in DBUtilsLease:"+ex);
            throw new DBException("Error while binding lease.", ex);

        }
    }

    public static Long getNextId(org.xmldb.api.base.Collection collection){
        try {
            String xQuery = "let $doc := doc($document)" +
                    "return $doc//lease-next-id/text()";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/bands/dataL.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            ResourceSet resultSet = service.execute(compiled);
            ResourceIterator results = resultSet.getIterator();
            if(results.hasMoreResources()) {
                Long id = Long.parseLong(results.nextResource().getContent().toString());
                if(results.hasMoreResources()){
                    log.log(Level.SEVERE, "DBException in DBUtilsLease:dataL.xml has more lease-next-id element");
                    throw new DBException("dataL.xml has more lease-next-id element");
                }
                return id;
            }else{
                log.log(Level.SEVERE, "DBException in DBUtilsLease: Next id does not exist");
                throw new DBException("Next id does not exist");
            }
        }catch (XMLDBException ex) {
            log.log(Level.SEVERE, "XMLDBException in DBUtilsLease:" + ex);
            throw new DBException("Error while getting next id", ex);
        }catch (NumberFormatException ex){
            log.log(Level.SEVERE, "NumberFormatException in DBUtilsLease:"+ex);
            throw new DBException("Error while parsing next id", ex);
        }
    }

    public static void incrementId(org.xmldb.api.base.Collection collection, Long id) {
        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update value $doc//lease-next-id with $nextId";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/bands/dataL.xml");
            service.declareVariable("nextId",++id);
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
        }catch (XMLDBException ex) {
            log.log(Level.SEVERE, "XMLDBException in DBUtilsLease:"+ex);
            throw new DBException("Error while incrementing", ex);
        }catch (NumberFormatException ex){
            log.log(Level.SEVERE, "NumberFormatException in DBUtilsLease:"+ex);
            throw new DBException("Error while parsing next id", ex);
        }
    }

    public static Lease parseLeaseFromXML(String xml){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {
                Lease lease = new Lease();
                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("lease");
                Element parent = (Element) a.item(0);
                lease.setId(Long.parseLong(parent.getAttribute("id")));

                a = parent.getElementsByTagName("customerId");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing customerId");
                    throw new LeaseException("Error while parsing customerId");
                }
                Element el = (Element) a.item(0);
                try {
                    CustomerManagerImpl customerManager = new CustomerManagerImpl(DBUtils.loadCustomerCollection());
                    lease.setCustomer(customerManager.getCustomer(Long.parseLong(el.getTextContent())));
                }catch(NumberFormatException ex){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing long");
                    throw new LeaseException("Error while parsing long");
                }catch (Exception ex) {
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while getting customer by id in lease");
                    throw new LeaseException("Error while getting customer by id in lease");
                }

                a = parent.getElementsByTagName("bandId");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while pasing bandId");
                    throw new LeaseException("Error while parsing bandId");
                }
                el = (Element) a.item(0);
                try {
                    BandManagerImpl bandManager = new BandManagerImpl(DBUtilsBand.loadBandCollection());
                    lease.setBand(bandManager.findBandById(Long.parseLong(el.getTextContent())));
                }catch(NumberFormatException ex){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing long");
                    throw new LeaseException("Error while parsing long");
                }catch (Exception ex) {
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while getting band by id in lease");
                    throw new LeaseException("Error while getting band by id in lease");
                }

                a = parent.getElementsByTagName("date");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing date");
                    throw new LeaseException("Error while parsing date");
                }
                el = (Element) a.item(0);
                try {
                    lease.setDate(Date.valueOf(el.getTextContent()));
                }catch(IllegalArgumentException ex){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing date");
                    throw new LeaseException("Error while parsing date");
                }

                a = parent.getElementsByTagName("place");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing place");
                    throw new LeaseException("Error while parsing place");
                }
                el = (Element) a.item(0);
                lease.setPlace(Region.valueOf(el.getTextContent()));
                try {
                    lease.setPlace(Region.valueOf(el.getTextContent()));
                }catch(NumberFormatException ex){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing region");
                    throw new LeaseException("Error while parsing region");
                }

                a = parent.getElementsByTagName("duration");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing duration");
                    throw new LeaseException("Error while parsing duration");
                }
                el = (Element) a.item(0);
                try {
                    lease.setDuration(Integer.parseInt(el.getTextContent()));
                }catch(NumberFormatException ex){
                    log.log(Level.SEVERE, "LeaseException in DBUtilsLease: Error while parsing integer");
                    throw new LeaseException("Error while parsing integer");
                }
                return lease;

            } catch (SAXException e) {
                log.log(Level.SEVERE, "SAXException in DBUtilsLease:"+e);
                throw new LeaseException("Error creating document from xml for parsing");
            } catch (IOException e) {
                log.log(Level.SEVERE, "IOException in DBUtilsLease:"+e);
                throw new LeaseException("Error parsing lease");
            }
        } catch (ParserConfigurationException ex) {
            log.log(Level.SEVERE, "ParseFormatException in DBUtilsLease:"+ex);
            throw new LeaseException("Error while configure parser", ex);
        }
    }

    public static Collection createLeaseCollection() throws IllegalAccessException, InstantiationException, ClassNotFoundException, XMLDBException {
        Properties configProperty = new ConfigProperty();

        Class c = Class.forName(configProperty.getProperty("db_driver"));
        Database database = (Database) c.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        Collection collection = DatabaseManager.getCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"), configProperty.getProperty("db_name"), configProperty.getProperty("db_password"));

        org.xmldb.api.base.Collection parent = DatabaseManager.getCollection(configProperty.getProperty("db_prefix"), configProperty.getProperty("db_name"), configProperty.getProperty("db_password"));
        CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
        mgt.createCollection(configProperty.getProperty("db_collection"));
        parent.close();
        collection = DatabaseManager.getCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"), configProperty.getProperty("db_name"), configProperty.getProperty("db_password"));

        XMLResource resource = (XMLResource) collection.createResource(configProperty.getProperty("db_leaseResourceName"), "XMLResource");
        resource.setContent("<leases></leases>");
        collection.storeResource(resource);

        resource = (XMLResource) collection.createResource(configProperty.getProperty("db_metaDataL"), "XMLResource");
        resource.setContent("<dataL><lease-next-id>1</lease-next-id></dataL>");
        collection.storeResource(resource);

        return collection;
    }

    public static Collection loadLeaseCollection() throws IllegalAccessException, InstantiationException, ClassNotFoundException, XMLDBException {
        Properties configProperty = new ConfigProperty();

        Class c = Class.forName(configProperty.getProperty("db_driver"));
        Database database = (Database) c.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        Collection collection = DatabaseManager.getCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"), configProperty.getProperty("db_name"), configProperty.getProperty("db_password"));

        return collection;
    }

    public static void dropLeaseDatabase() throws XMLDBException{
        Properties configProperty = new ConfigProperty();

        org.xmldb.api.base.Collection parent = DatabaseManager.getCollection(configProperty.getProperty("db_prefix"),configProperty.getProperty("db_name"),configProperty.getProperty("db_password"));
        CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");

        mgt.removeCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"));
        parent.close();
    }

    public static void createIfNotExistsLeaseResource()  throws IllegalAccessException, InstantiationException, ClassNotFoundException, XMLDBException {
        Properties configProperty = new ConfigProperty();
        Collection collection = DBUtils.loadOrCreateDatabaseCollection();

        XMLResource resource = (XMLResource)collection.getResource(configProperty.getProperty("db_leaseResourceName"));
        if (resource == null) {
            resource = (XMLResource) collection.createResource(configProperty.getProperty("db_leaseResourceName"), "XMLResource");
            resource.setContent("<leases></leases>");
            collection.storeResource(resource);

            resource = (XMLResource) collection.createResource(configProperty.getProperty("db_metaDataL"), "XMLResource");
            resource.setContent("<dataL><lease-next-id>1</lease-next-id></dataL>");
            collection.storeResource(resource);

        }
    }
}
