package cz.muni.fi.pv168.bandsproject;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmldb.api.base.*;
import sun.tools.jar.Main;
import org.exist.xmldb.XQueryService;
import java.util.Collection;

/**
 * Created by Lenka on 9.3.2016.
 */
public class CustomerManagerImpl implements CustomerManager {
    private org.xmldb.api.base.Collection collection;

    public CustomerManagerImpl(org.xmldb.api.base.Collection collection) {
        this.collection = collection;
    }

    private static final Logger log = Logger.getLogger(Main.class.getName());

    @Override
    public void createCustomer(Customer customer) {
        validate(customer);
        log.log(Level.INFO, "Create customer in customer manager: " + customer);
        if (customer.getId() == null) {
            customer.setId(DBUtils.getNextId(collection));
        }
        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update insert element customer{ " +
                    "attribute id {$id}, " +
                    "element name {$name}, " +
                    "element phoneNumber {$phoneNumber}, " +
                    "element address {$address} " +
                    "} into $doc/customers";
            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/customers.xml");
            DBUtils.bindCustomerToXQuery(customer, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Create customer in customer manager "+ customer + " is ok.");
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "DB exception: " + ex);
            throw new DBException("Error while creating new customer", ex);
        }

        DBUtils.incrementId(collection, Long.max(customer.getId(),DBUtils.getNextId(collection)));
    }

    @Override
    public void updateCustomer(Customer customer) {
        log.log(Level.INFO, "Update customer "+customer+" in customer manager");
        validate(customer);
        if(customer.getId() == null){
            log.log(Level.SEVERE, "CustomerException : Customer id is null");
            throw new CustomerException("Customer id is null");
        }

        if(customer.getId() < 0){
            log.log(Level.SEVERE, "CustomerException : Customer id is negative");
            throw new CustomerException("Customer id is negative");
        }

        if(getCustomer(customer.getId()) == null){
            log.log(Level.SEVERE, "CustomerException : There is no customer with id: " + customer.getId() + " in DB");
            throw new CustomerException("There is no customer with id: " + customer.getId() + " in DB");
        }

        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update replace $doc/customers/customer[@id=$id] with " +
                    "element customer{ " +
                    "attribute id {$id}, " +
                    "element name {$name}, " +
                    "element phoneNumber {$phoneNumber}, " +
                    "element address {$address}}";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/customers.xml");
            DBUtils.bindCustomerToXQuery(customer, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Update customer is ok");

        } catch (XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while updating customer", ex);
        }
    }

    @Override
    public void deleteCustomer(Customer customer) {
        log.log(Level.INFO, "Delete customer "+customer+" in customer manager");

        if(customer == null){
            log.log(Level.SEVERE, "CustomerException : Customer is null");
            throw new CustomerException("Customer is null");
        }
        if(customer.getId() == null){
            log.log(Level.SEVERE, "CustomerException : Customer id is null");
            throw new CustomerException("Customer id is null");
        }

        if(customer.getId() < 0){
            log.log(Level.SEVERE, "CustomerException : Customer id is negative");
            throw new CustomerException("Customer id is negative");
        }

        if(getCustomer(customer.getId()) == null){
            log.log(Level.SEVERE, "CustomerException : There is no customer with id: " + customer.getId() + " in DB");
            throw new CustomerException("There is no customer with id: " + customer.getId() + " in DB");
        }

        try {
            String xQuery = "let $doc := doc($document)" +
                    "return update delete $doc/customers/customer[@id=$id]";

            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/customers.xml");
            DBUtils.bindCustomerToXQuery(customer, service);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            service.execute(compiled);
            log.log(Level.INFO, "Delete customer is ok");
        } catch (XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while deleting customer", ex);
        }
    }

    @Override
    public Customer getCustomer(Long id) {
        log.log(Level.INFO, "Get customer by ID "+id+" in customer manager");
        if(id == null){
            log.log(Level.SEVERE, "Customer exception : id is null");
            throw new CustomerException("id is null");
        }
        if(id < 0){
            log.log(Level.SEVERE, "Customer exception : id is negative");
            throw new CustomerException("id is negative");
        }
        try {
            String xQuery = "let $doc := doc($document) " +
                    "return $doc/customers/customer[@id=$id]";
            XQueryService service = (XQueryService) collection.getService("XQueryService", "1.0");

            service.declareVariable("document", "/db/bands/customers.xml");
            service.declareVariable("id", id);

            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(xQuery);

            ResourceSet res = service.execute(compiled);
            ResourceIterator it = res.getIterator();
            if(it.hasMoreResources()){
                Resource resource = it.nextResource();
                Customer result = DBUtils.parseCustomerFromXML(resource.getContent().toString());
                if(it.hasMoreResources()){
                    log.log(Level.SEVERE, "Customer exception : More customer with same id");
                    throw new CustomerException("More customer with same id");
                }
                log.log(Level.INFO, "Get customer by ID is OK");
                return result;
            }
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while creating new customer",ex);
        }
        return null;
    }

    @Override
    public Collection<Customer> getAllCustomers() {
        log.log(Level.INFO, "Get all customers in customer manager");

        Collection<Customer> resultList;
        try {
            resultList = DBUtils.selectCustomersFromDBWhere(collection);
        }catch(XMLDBException ex){
            log.log(Level.SEVERE, "XMLDBException:"+ex);
            throw new DBException("Error while getting all customers", ex);
        }
        log.log(Level.INFO, "Get all customers is OK - number of customers: " + resultList.size());
        return resultList;
}

    /**
     *
     * @param customer
     * @throws IllegalArgumentException
     */
    private void validate(Customer customer) throws IllegalArgumentException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getName() == null) {
            throw new IllegalArgumentException("customer name is null");
        }
        if(customer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("customer phone number is null");
        }
        if(customer.getAddress() == null) {
            throw new IllegalArgumentException("customer adress is null");
        }
    }
}
