package cz.muni.fi.pv168.bandsproject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;

/**
 *
 * @author Tomas
 */
@RunWith(SpringJUnit4ClassRunner.class) //Spring se zúčastní unit testů
@ContextConfiguration(classes = {MySpringTestConfig.class}) //konfigurace je ve třídě MySpringTestConfig
@Transactional //každý test poběží ve vlastní transakci, která bude na konci rollbackována
public class CustomerManagerImplTest {
    @Autowired
    private CustomerManager customerManager;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createCustomer() {
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        customerManager.createCustomer(customer);

        Long customerId = customer.getId();
        assertThat("saved customer has null ID", customer.getId(), is(not(equalTo(null))));

        Customer result = customerManager.getCustomer(customerId);
        assertThat("loaded user is the same instance", result, is(not(sameInstance(customer))));
        
        assertDeepEquals(customer, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() throws Exception {
        customerManager.createCustomer(null);
    }

    @Test
    public void createCustomerWithWrongValues() {
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");

        customer.setId(1L);
        try {
            customerManager.createCustomer(customer);
            fail("should refuse assigned id");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        customer = newCustomer(null, "666 123 456", "Kartouzska 69");
        try {
            customerManager.createCustomer(customer);
            fail("no name of customer detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        customer = newCustomer("Pepa", null, "Kartouzska 69");
        try {
            customerManager.createCustomer(customer);
            fail("no phone number of customer detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        customer = newCustomer("Pepa", "666 123 456", null);
        try {
            customerManager.createCustomer(customer);
            fail("no address of customer detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }



    @Test
    public void updateCustomer() {
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        Customer customer2 = newCustomer("Alojz", "158 155 150", "Route 66");
        customerManager.createCustomer(customer);
        customerManager.createCustomer(customer2);
        Long customerId = customer.getId();

        
        customer.setName("Jozef");
        
        customerManager.updateCustomer(customer);
        //load from database
        customer = customerManager.getCustomer(customerId);
        //new style assertions
        assertThat("name was not changed", customer.getName(), is(equalTo("Jozef")));
        assertThat("phone number was changed when changing name", customer.getPhoneNumber(), is(equalTo("666 123 456")));
        assertThat("address was changed when changing name", customer.getAddress(), is(equalTo("Kartouzska 69")));

        //change row value to 0
        customer.setPhoneNumber("000 111 222");
        customerManager.updateCustomer(customer);
        //load from database
        customer = customerManager.getCustomer(customerId);
        //old style assertions
        assertEquals("Jozef", customer.getName());
        assertEquals("000 111 222", customer.getPhoneNumber());
        assertEquals("Kartouzska 69", customer.getAddress());

        customer.setAddress("Kartoozska 69");
        customerManager.updateCustomer(customer);
        customer = customerManager.getCustomer(customerId);
        assertEquals("Jozef", customer.getName());
        assertEquals("000 111 222", customer.getPhoneNumber());
        assertEquals("Kartoozska 69", customer.getAddress());

        // Check if updates didn't affected other records
        assertDeepEquals(customer2, customerManager.getCustomer(customer2.getId()));
    }

    @Test
    public void updateCustomerWithWrongAttributes() {

        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        customerManager.createCustomer(customer);
        Long customerId = customer.getId();

        try {
            customerManager.updateCustomer(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomer(customerId);
            customer.setId(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomer(customerId);
            customer.setName(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomer(customerId);
            customer.setPhoneNumber(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = customerManager.getCustomer(customerId);
            customer.setAddress(null);
            customerManager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }

    @Test
    public void deleteCustomer() {

        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        Customer customer2 = newCustomer("Alojz", "158 155 150", "Route 66");
        customerManager.createCustomer(customer);
        customerManager.createCustomer(customer2);

        assertNotNull(customerManager.getCustomer(customer.getId()));
        assertNotNull(customerManager.getCustomer(customer2.getId()));

        customerManager.deleteCustomer(customer);

        assertNull(customerManager.getCustomer(customer.getId()));
        assertNotNull(customerManager.getCustomer(customer2.getId()));
    }

    @Test
    public void deleteCustomerWithWrongAttributes() {

        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        customerManager.createCustomer(customer);
        
        //null id
        expectedException.expect(IllegalArgumentException.class);
        customer.setId(null);
        customerManager.deleteCustomer(null);

        //null id
        expectedException.expect(IllegalArgumentException.class);
        customer.setId(1L);
        customerManager.deleteCustomer(null);
    }

    public Customer newCustomer(String name, String phoneNumber, String address){
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);

        return customer;
    }

    private void assertDeepEquals(Customer expected, Customer actual){
        assertEquals("id is not equal", expected.getId(), actual.getId());
        assertEquals("name is not equal", expected.getName(), actual.getName());
        assertEquals("phone number is not equal", expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals("address is not equal", expected.getAddress(), actual.getAddress());
    }
}