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
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import org.xmldb.api.base.Collection;

/**
 * Created by Lenka on 3.5.2016.
 */
public class DBUtilsBand {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static java.util.Collection<Band> selectBandsFromDBWhere(Collection collection, String condition, String[] arguments) throws XMLDBException {
        List<Band> resultList = new ArrayList<>();
        String xQuery;

        if(condition != null && !condition.isEmpty()) {
            xQuery = "let $doc := doc($document) " +
                    "return $doc/bands/band[" + condition + "]";
        }else{
            xQuery = "let $doc := doc($document) " +
                    "return $doc/bands/band";
        }

        XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

        service.declareVariable("document", "/db/bands/bands.xml");

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
            resultList.add(parseBandFromXML(resource.getContent().toString()));
        }
        return resultList;
    }

    public static java.util.Collection<Band> selectBandsFromDBWhere(Collection collection) throws XMLDBException{
        return selectBandsFromDBWhere(collection, null, null);
    }

    public static java.util.Collection<Band> selectBandsFromDBWhere(Collection collection, String condition) throws XMLDBException{
        return selectBandsFromDBWhere(collection, condition, null);
    }

    public static void bindBandToXQuery(Band band, XQueryService service){
        try {
            service.declareVariable("id", band.getId());
            service.declareVariable("name", band.getName());
            service.declareVariable("styles", band.getStyles().toString());
            service.declareVariable("region", band.getRegion().toString());
            service.declareVariable("pricePerHour", band.getPricePerHour());
            service.declareVariable("rate", band.getRate());
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException in DBUtils:"+ex);
            throw new DBException("Error while binding band.", ex);

        }
    }

    public static Long getNextId(org.xmldb.api.base.Collection collection){
        try {
            String xQuery = "let $doc := doc($document)" +
                    "return $doc//band-next-id/text()";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/bands/dataB.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            ResourceSet resultSet = service.execute(compiled);
            ResourceIterator results = resultSet.getIterator();
            if(results.hasMoreResources()) {
                Long id = Long.parseLong(results.nextResource().getContent().toString());
                if(results.hasMoreResources()){
                    log.log(Level.SEVERE, "DBException in DBUtils:dataB.xml has more band-next-id element");
                    throw new DBException("dataB.xml has more band-next-id element");
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
                    "return update value $doc//band-next-id with $nextId";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/bands/dataB.xml");
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

    public static Band parseBandFromXML(String xml){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {
                Band band = new Band();
                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("band");
                Element parent = (Element) a.item(0);
                band.setId(Long.parseLong(parent.getAttribute("id")));

                a = parent.getElementsByTagName("name");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "BandException in DBUtils: Error while parsing name");
                    throw new BandException("Error while parsing name");
                }
                Element el = (Element) a.item(0);
                band.setBandName(el.getTextContent());

                a = parent.getElementsByTagName("styles");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "BandException in DBUtils: Error while parsing styles");
                    throw new BandException("Error while parsing styles");
                }
                el = (Element) a.item(0);
                //System.out.println("********* styles: " + el);    //dava null
                //band.setStyles(el.getTextContent()); //je to string a potrebujem list!

                a = parent.getElementsByTagName("region");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "BandException in DBUtils: Error while parsing region");
                    throw new BandException("Error while parsing region");
                }
                el = (Element) a.item(0);
                band.setRegion(Region.valueOf(el.getTextContent()));

                a = parent.getElementsByTagName("pricePerHour");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "BandException in DBUtils: Error while parsing pricePerHour");
                    throw new BandException("Error while parsing pricePerHour");
                }
                el = (Element) a.item(0);
                try {
                    band.setPricePerHour(Double.parseDouble(el.getTextContent()));
                }catch(NumberFormatException ex){
                    log.log(Level.SEVERE, "CarException in DBUtils: Error while parsing double");
                    throw new BandException("Error while parsing double");
                }

                a = parent.getElementsByTagName("rate");
                if(a.getLength() != 1){
                    log.log(Level.SEVERE, "BandException in DBUtils: Error while parsing rate");
                    throw new BandException("Error while parsing rate");
                }
                el = (Element) a.item(0);
                try {
                    band.setRate(Double.parseDouble(el.getTextContent()));
                }catch(NumberFormatException ex){
                    log.log(Level.SEVERE, "CarException in DBUtils: Error while parsing double");
                    throw new BandException("Error while parsing double");
                }

                return band;

            } catch (SAXException e) {
                log.log(Level.SEVERE, "SAXException in DBUtils:"+e);
                throw new BandException("Error creating document from xml for parsing");
            } catch (IOException e) {
                log.log(Level.SEVERE, "IOException in DBUtils:"+e);
                throw new BandException("Error parsing band");
            }
        } catch (ParserConfigurationException ex) {
            log.log(Level.SEVERE, "ParseFormatException in DBUtils:"+ex);
            throw new BandException("Error while configure parser", ex);
        }
    }

    public static Collection loadOrCreateBandCollection() throws IllegalAccessException, InstantiationException, ClassNotFoundException, XMLDBException {
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

            XMLResource resource = (XMLResource) collection.createResource(configProperty.getProperty("db_bandResourceName"), "XMLResource");
            resource.setContent("<bands></bands>");
            collection.storeResource(resource);

            resource = (XMLResource) collection.createResource(configProperty.getProperty("db_metaDataBand"), "XMLResource");
            resource.setContent("<dataB><band-next-id>1</band-next-id></dataB>");
            collection.storeResource(resource);
        }

        return collection;
    }

    public static void dropBandDatabase() throws XMLDBException{
        Properties configProperty = new ConfigProperty();

        org.xmldb.api.base.Collection parent = DatabaseManager.getCollection(configProperty.getProperty("db_prefix"),configProperty.getProperty("db_name"),configProperty.getProperty("db_password"));
        CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");

        mgt.removeCollection(configProperty.getProperty("db_prefix") + configProperty.getProperty("db_collection"));
        parent.close();
    }
}
