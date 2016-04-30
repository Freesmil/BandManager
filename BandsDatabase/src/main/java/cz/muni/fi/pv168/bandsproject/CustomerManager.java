package cz.muni.fi.pv168.bandsproject;

import java.util.Collection;

/**
 * Created by Lenka on 9.3.2016.
 */
public interface CustomerManager {

    public void createCustomer(Customer customer);

    public void updateCustomer(Customer customer);

    public void deleteCustomer(Customer customer);

    public Customer getCustomer(Long id);
    
    public Collection<Customer> getAllCustomers();
}
