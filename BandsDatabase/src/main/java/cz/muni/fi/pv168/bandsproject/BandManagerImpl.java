package cz.muni.fi.pv168.bandsproject;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmldb.api.base.*;
import sun.tools.jar.Main;
import org.exist.xmldb.XQueryService;
import java.util.Collection;

/**
 * Created by Lenka on 9.3.2016.
 */
public class BandManagerImpl implements BandManager{
    private org.xmldb.api.base.Collection collection;

    public BandManagerImpl(org.xmldb.api.base.Collection collection) {
        this.collection = collection;
    }

    private static final Logger log = Logger.getLogger(Main.class.getName());


    @Override
    public void createBand(Band band) throws ServiceFailureException {
        validate(band);log.log(Level.INFO, "Create band in band manager: " + band);
        if(band.getId() != null){
            log.log(Level.SEVERE, "Band exception: Band id must be null");
            throw new BandException("Band id must be null");
        }
        band.setId(DBUtilsBand.getNextId(collection));
        try {
            String xQuery = "let $doc := doc($document) " +
                    "return update insert element band{ " +
                    "attribute id {$id}, " +
                    "element name {$name}, " +
                    "element styles{ ";
            for (int i = 0; i < band.getStyles().size(); i++) {
                xQuery += "element style{$style" + i + "}, ";
            }
            xQuery = xQuery.substring(0, xQuery.length() - 2);
            xQuery += " }, element region {$region}, " +
                    "element pricePerHour {$pricePerHour}, " +
                    "element rate {$rate} " +
                    "} into $doc/bands";
            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/bands.xml");
            DBUtilsBand.bindBandToXQuery(band, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Create band in band manager "+ band + " is ok.");
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "DB exception: " + ex);
            throw new DBException("Error while creating new band", ex);
        }

        DBUtilsBand.incrementId(collection, band.getId());
    }
    
    @Override
    public void updateBand(Band band) throws ServiceFailureException {
        log.log(Level.INFO, "Update band "+band+" in band manager");
        validate(band);
        if(band.getId() == null){
            log.log(Level.SEVERE, "BandException : Band id is null");
            throw new BandException("Band id is null");
        }

        if(band.getId() < 0){
            log.log(Level.SEVERE, "BandException : Band id is negative");
            throw new BandException("Band id is negative");
        }

        if(findBandById(band.getId()) == null){
            log.log(Level.SEVERE, "BandException : There is no band with id: " + band.getId() + " in DB");
            throw new BandException("There is no band with id: " + band.getId() + " in DB");
        }

        try {
            String xQuery = "let $doc := doc($document) " +
                    "return update replace $doc/bands/band[@id=$id] with " +
                    "element band{ " +
                    "attribute id {$id}, " +
                    "element name {$name}, " +
                    "element styles{ ";
            for (int i = 0; i < band.getStyles().size(); i++) {
                xQuery += "element style{$style" + i + "}, ";
            }
            xQuery = xQuery.substring(0, xQuery.length() - 2);
            xQuery += " }, element region {$region}, " +
                    "element pricePerHour {$pricePerHour}, " +
                    "element rate {$rate}}";
            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/bands.xml");
            DBUtilsBand.bindBandToXQuery(band, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Update band is ok");

        } catch (XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while updating band", ex);
        }
    }

    @Override
    public void deleteBand(Band band) throws ServiceFailureException {
        log.log(Level.INFO, "Delete band "+band+" in band manager");

        if(band == null){
            log.log(Level.SEVERE, "BandException : Band is null");
            throw new BandException("Band is null");
        }
        if(band.getId() == null){
            log.log(Level.SEVERE, "BandException : Band id is null");
            throw new BandException("Band id is null");
        }

        if(band.getId() < 0){
            log.log(Level.SEVERE, "BandException : Band id is negative");
            throw new BandException("Band id is negative");
        }

        if(findBandById(band.getId()) == null){
            log.log(Level.SEVERE, "BandException : There is no band with id: " + band.getId() + " in DB");
            throw new BandException("There is no band with id: " + band.getId() + " in DB");
        }

        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update delete $doc/bands/band[@id=$id]";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/bands.xml");
            DBUtilsBand.bindBandToXQuery(band, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Delete band is ok");
        } catch (XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while deleting band", ex);
        }
    }
    
    @Override
    public void createStylesBand(Long id, List<Style> styles) throws ServiceFailureException {
        /*for(Style style : styles){
            SimpleJdbcInsert insertStyles = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("band_styles").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>(2);
            parameters.put("idBand", id);
            parameters.put("style", style.ordinal());
            insertStyles.executeAndReturnKey(parameters);
        }*/
    }
    
    @Override
    public void updateStylesBand(Long id, List<Style> styles) throws ServiceFailureException {
        /*deleteStylesBand(id);
        for(Style style : styles){
            SimpleJdbcInsert insertStyles = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("band_styles").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>(2);
            parameters.put("idBand", id);
            parameters.put("style", style.ordinal());
            insertStyles.executeAndReturnKey(parameters);
        }*/
    }
    
    @Override
    public void deleteStylesBand(Long id) throws ServiceFailureException {
        /*if (id == null) {
            throw new IllegalArgumentException("band is null");
        }
        
        jdbcTemplateObject.update("DELETE FROM band_styles WHERE idBand = ?", id);*/
    }
    
    @Override
    public List<Style> getStylesBand(Long id) throws ServiceFailureException {
       /* List<Style> styles = jdbcTemplateObject.query("SELECT style FROM band_styles WHERE idBand = ?", (ResultSet rs, int rowNum) -> Style.values()[rs.getInt("style")], id);

        return styles;*/
        return null;
    }

    @Override
    public Collection<Band> getAllBands() {
        log.log(Level.INFO, "Get all bands in band manager");

        Collection<Band> resultList;
        try {
            resultList = DBUtilsBand.selectBandsFromDBWhere(collection);
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting all bands", ex);
        }
        log.log(Level.INFO, "Get all bands is OK");
        return resultList;
    }
    
    @Override
    public Band findBandById(Long id) throws ServiceFailureException {
        log.log(Level.INFO, "Get band by ID "+id+" in band manager");
        if(id == null){
            log.log(Level.SEVERE, "Band exception : id is null");
            throw new BandException("id is null");
        }
        if(id < 0){
            log.log(Level.SEVERE, "Band exception : id is negative");
            throw new BandException("id is negative");
        }
        try {
            String xQuery = "let $doc := doc($document) " +
                    "return $doc/bands/band[@id=$id]";
            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/bands.xml");
            service.declareVariable("id", id);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            ResourceSet res = service.execute(compiled);
            ResourceIterator it = res.getIterator();
            if(it.hasMoreResources()){
                Resource resource = it.nextResource();
                Band result = DBUtilsBand.parseBandFromXML(resource.getContent().toString());
                if(it.hasMoreResources()){
                    log.log(Level.SEVERE, "Band exception : More band with same id");
                    throw new BandException("More band with same id");
                }
                log.log(Level.INFO, "Get band by ID is OK");
                return result;
            }
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while creating new band",ex);
        }
        return null;
    }

    @Override
    public Collection<Band> findBandByName(String name) throws ServiceFailureException {
        log.log(Level.INFO, "Get car by name " + name + " in band manager");
        if(name == null){
            log.log(Level.SEVERE, "IlegalArgumentException : name is null");
            throw new IllegalArgumentException("name is null");
        }

        if(name.isEmpty()){
            log.log(Level.SEVERE, "IlegalArgumentException : is empty");
            throw new IllegalArgumentException("name is empty");
        }

        Collection<Band> resultList;
        try {
            resultList = DBUtilsBand.selectBandsFromDBWhere(collection, "name=$argument0", new String[]{name});
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting bands by name", ex);
        }
        log.log(Level.INFO, "Get band by name is OK");
        return resultList;
    }

    @Override
    public Collection<Band> findBandByStyles(List<Style> styles) {
        log.log(Level.INFO, "Get band by styles in band manager");

        if(styles == null){
            log.log(Level.SEVERE, "IlegalArgumentException : styles is null");
            throw new IllegalArgumentException("styles is null");
        }

        Collection<Band> resultList = new ArrayList<>();
        try {
            String condition = "";
            for (Style st : styles){
                condition += "styles/style=\"" + st.toString() + "\" or ";
            }
            condition = condition.substring(0, condition.length() - 4);
            resultList.addAll(DBUtilsBand.selectBandsFromDBWhere(collection, condition));
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting band by " + styles, ex);
        }
        log.log(Level.INFO, "Get band by styles is OK");
        return resultList;

    }

    @Override
    public Collection<Band> findBandByRegion(List<Region> regions) {
        log.log(Level.INFO, "Get band by region in band manager");

        if(regions == null){
            log.log(Level.SEVERE, "IlegalArgumentException : regions is null");
            throw new IllegalArgumentException("regions is null");
        }

        Collection<Band> resultList = new ArrayList<>();
        try {
            for (Region reg : regions){
                resultList.addAll(DBUtilsBand.selectBandsFromDBWhere(collection, "region=$argument0", new String[]{String.valueOf(reg)}));
            }
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting band by " + regions, ex);
        }
        log.log(Level.INFO, "Get band by regions is OK");
        return resultList;
    }

    @Override
    public Collection<Band> findBandByPriceRange(Double from, Double to) throws ServiceFailureException {
        log.log(Level.INFO, "Get band by price from " + from + " to " + to + " in band manager");

        if(from < 0){
            log.log(Level.SEVERE, "IlegalArgumentException : price from is negative");
            throw new IllegalArgumentException("from is negative");
        }
        if(to < 0){
            log.log(Level.SEVERE, "IlegalArgumentException : price to is negative");
            throw new IllegalArgumentException("to is negative");
        }
        if(to < from){
            log.log(Level.SEVERE, "IlegalArgumentException : to is less than from");
            throw new IllegalArgumentException("to is less than from");
        }

        Collection<Band> resultList;
        try {
            resultList = DBUtilsBand.selectBandsFromDBWhere(collection,
                    "pricePerHour>=number($argument0) and pricePerHour<=number($argument1)",
                    new String[]{String.valueOf(from), String.valueOf(to)});
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting bands less price", ex);
        }
        log.log(Level.INFO, "Get band by price is OK");
        return resultList;
    }

    @Override
    public Collection<Band> findBandByRate(Double from) throws ServiceFailureException {
        log.log(Level.INFO, "Get band by rate from " + from + " in band manager");

        if(from < 0){
            log.log(Level.SEVERE, "IlegalArgumentException : price from is negative");
            throw new IllegalArgumentException("from is negative");
        }

        Collection<Band> resultList;
        try {
            resultList = DBUtilsBand.selectBandsFromDBWhere(collection,
                    "rate>=number($argument0)", new String[]{String.valueOf(from)});
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting bands less rate", ex);
        }
        log.log(Level.INFO, "Get band by rate is OK");
        return resultList;
    }

    /**
     *
     * @param band
     * @throws IllegalArgumentException
     */
    private void validate(Band band) throws IllegalArgumentException {
        if (band == null) {
            throw new IllegalArgumentException("band is null");
        }
        if (band.getName() == null) {
            throw new IllegalArgumentException("band name is null");
        }
        if (band.getStyles() == null) {
            throw new IllegalArgumentException("band styles is null");
        }
        if (band.getRegion() == null) {
            throw new IllegalArgumentException("band region is null");
        }
        if (band.getPricePerHour() < 0) {
            throw new IllegalArgumentException("band price per hour is negative");
        }
        if (band.getRate() < 0) {
            throw new IllegalArgumentException("band rate is negative");
        }
    }
}

