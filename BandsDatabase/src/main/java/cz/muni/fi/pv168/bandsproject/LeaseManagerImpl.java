package cz.muni.fi.pv168.bandsproject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmldb.api.base.*;
import sun.tools.jar.Main;
import org.exist.xmldb.XQueryService;
import java.util.Collection;

/**
 * Created by Lenka on 9.3.2016.
 */
public class LeaseManagerImpl implements LeaseManager{
    private org.xmldb.api.base.Collection collection;

    public LeaseManagerImpl(org.xmldb.api.base.Collection collection) {
        this.collection = collection;
    }

    private static final Logger log = Logger.getLogger(Main.class.getName());

    @Override 
    public void createLease(Lease lease) throws ServiceFailureException {
        validate(lease);
        log.log(Level.INFO, "Create lease in lease manager: " + lease);
        if(lease.getId() != null){
            log.log(Level.SEVERE, "Lease exception: Lease id must be null");
            throw new LeaseException("Lease id must be null");
        }
        lease.setId(DBUtilsLease.getNextId(collection));
        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update insert element lease{ " +
                    "attribute id {$id}, " +
                    "element customerId {$customerId}, " +
                    "element bandId {$bandId}, " +
                    "element date {$date}, " +
                    "element place {$place}, " +
                    "element duration {$duration} " +
                    "} into $doc/leases";
            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/leases.xml");
            DBUtilsLease.bindLeaseToXQuery(lease, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Create lease in lease manager "+ lease + " is ok.");
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "DB exception: " + ex);
            throw new DBException("Error while creating new lease", ex);
        }

        DBUtilsLease.incrementId(collection, lease.getId());
    }

    @Override
    public void updateLease(Lease lease) throws ServiceFailureException {
        validate(lease);
        log.log(Level.INFO, "Update lease "+lease+" in lease manager");
        if(lease.getId() == null){
            log.log(Level.SEVERE, "LeaseException : Lease id is null");
            throw new LeaseException("Lease id is null");
        }

        if(lease.getId() < 0){
            log.log(Level.SEVERE, "LeaseException : Lease id is negative");
            throw new LeaseException("Lease id is negative");
        }

        if(findLeaseById(lease.getId()) == null){
            log.log(Level.SEVERE, "LeaseException : There is no lease with id: " + lease.getId() + " in DB");
            throw new LeaseException("There is no lease with id: " + lease.getId() + " in DB");
        }

        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update replace $doc/leases/lease[@id=$id] with " +
                    "element lease{ " +
                    "attribute id {$id}, " +
                    "element customerId {$customerId}, " +
                    "element bandId {$bandId}, " +
                    "element date {$date}, " +
                    "element place {$place}, " +
                    "element duration {$duration}}";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/leases.xml");
            DBUtilsLease.bindLeaseToXQuery(lease, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Update lease is ok");

        } catch (XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while updating lease", ex);
        }
    }

    @Override
    public void deleteLease(Lease lease) throws ServiceFailureException {
        log.log(Level.INFO, "Delete lease "+lease+" in lease manager");

        if(lease == null){
            log.log(Level.SEVERE, "LeaseException : Lease is null");
            throw new LeaseException("Lease is null");
        }
        if(lease.getId() == null){
            log.log(Level.SEVERE, "LeaseException : Lease id is null");
            throw new LeaseException("Lease id is null");
        }

        if(lease.getId() < 0){
            log.log(Level.SEVERE, "LeaseException : Lease id is negative");
            throw new LeaseException("Lease id is negative");
        }

        if(findLeaseById(lease.getId()) == null){
            log.log(Level.SEVERE, "LeaseException : There is no lease with id: " + lease.getId() + " in DB");
            throw new LeaseException("There is no lease with id: " + lease.getId() + " in DB");
        }

        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update delete $doc/leases/lease[@id=$id]";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/leases.xml");
            DBUtilsLease.bindLeaseToXQuery(lease, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Delete lease is ok");
        } catch (XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while deleting lease", ex);
        }
    }

    @Override
    public Lease findLeaseById(Long id) {
        log.log(Level.INFO, "Get lease by ID "+id+" in lease manager");
        if(id == null){
            log.log(Level.SEVERE, "Lease exception : id is null");
            throw new LeaseException("id is null");
        }
        if(id < 0){
            log.log(Level.SEVERE, "Lease exception : id is negative");
            throw new LeaseException("id is negative");
        }
        try {
            String xQuery = "let $doc := doc($document) " +
                    "return $doc/leases/lease[@id=$id]";
            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/leases.xml");
            service.declareVariable("id", id);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            ResourceSet res = service.execute(compiled);
            ResourceIterator it = res.getIterator();
            if(it.hasMoreResources()){
                Resource resource = it.nextResource();
                Lease result = DBUtilsLease.parseLeaseFromXML(resource.getContent().toString());
                if(it.hasMoreResources()){
                    log.log(Level.SEVERE, "Lease exception : More lease with same id");
                    throw new LeaseException("More lease with same id");
                }
                log.log(Level.INFO, "Get lease by ID is OK");
                return result;
            }
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while creating new lease",ex);
        }
        return null;
    }

    @Override
    public Collection<Lease> findAllLeases() {
        log.log(Level.INFO, "Get all leases in lease manager");

        Collection<Lease> resultList;
        try {
            resultList = DBUtilsLease.selectLeasesFromDBWhere(collection);
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting all leases", ex);
        }
        log.log(Level.INFO, "Get all leases is OK");
        return resultList;
    }

    @Override
    public Collection<Lease> findLeasesForBand(Band band) throws ServiceFailureException {
        log.log(Level.INFO, "Get lease for band " + band + " in lease manager");
        if(band == null){
            log.log(Level.SEVERE, "IlegalArgumentException : band is null");
            throw new IllegalArgumentException("band is null");
        }

        Collection<Lease> resultList;
        try {
            resultList = DBUtilsLease.selectLeasesFromDBWhere(collection, "bandId=$argument0", new String[]{String.valueOf(band.getId())});
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting leases for band", ex);
        }
        log.log(Level.INFO, "Get leases for band is OK");
        return resultList;
    }

    @Override
    public Collection<Lease> findLeasesForCustomer(Customer customer) throws ServiceFailureException {
        log.log(Level.INFO, "Get lease for band " + customer + " in lease manager");
        if(customer == null){
            log.log(Level.SEVERE, "IlegalArgumentException : customer is null");
            throw new IllegalArgumentException("customer is null");
        }

        Collection<Lease> resultList;
        try {
            resultList = DBUtilsLease.selectLeasesFromDBWhere(collection, "customerId=$argument0", new String[]{String.valueOf(customer.getId())});
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting leases for customer", ex);
        }
        log.log(Level.INFO, "Get leases for customer is OK");
        return resultList;
    }

    /**
     *
     * @param lease
     * @throws IllegalArgumentException
     */
    private void validate(Lease lease) throws IllegalArgumentException {
        if (lease == null) {
            throw new IllegalArgumentException("lease is null");
        }
        if (lease.getCustomer() == null) {
            throw new IllegalArgumentException("customer in lease is null");
        }
        if (lease.getBand() == null) {
            throw new IllegalArgumentException("band in lease is null");
        }
        if (lease.getDate() == null) {
            throw new IllegalArgumentException("date in lease is null");
        }
        if (lease.getPlace() == null) {
            throw new IllegalArgumentException("place is null");
        }
        if (lease.getDuration() <= 0) {
            throw new IllegalArgumentException("duration is zero or negative");
        }
    }
}
