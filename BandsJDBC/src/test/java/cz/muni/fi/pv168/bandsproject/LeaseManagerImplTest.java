package cz.muni.fi.pv168.bandsproject;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by Lenka on 18.3.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class) //Spring se zúčastní unit testů
@ContextConfiguration(classes = {MySpringTestConfig.class}) //konfigurace je ve třídě MySpringTestConfig
@Transactional //každý test poběží ve vlastní transakci, která bude na konci rollbackována
public class LeaseManagerImplTest {
    @Autowired
    private LeaseManager leaseManager;
    @Autowired
    private BandManager bandManager;
    @Autowired
    private CustomerManager customerManager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void testCreateLease() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band1 = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        bandManager.createBand(band1);
        
        Customer customer1 = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        customerManager.createCustomer(customer1);
        
        Lease lease1 = newLease(band1, customer1, "08.08.2016", Region.jihomoravsky, 2);
        leaseManager.createLease(lease1);
        assertNotNull(lease1.getId());
        
        List<Style> anotherStyles = new ArrayList<>();
        anotherStyles.add(Style.disco);
        anotherStyles.add(Style.hipHop);
        anotherStyles.add(Style.dnb);
        Band anotherBand = newBand("Some DiscoHipHopDnbBand", anotherStyles, Region.plzensky, 149.99, 2.3);
        bandManager.createBand(anotherBand);
        
        Customer customer2 = newCustomer("Alojz", "158 155 150", "Route 66");
        customerManager.createCustomer(customer2);
        
        Lease lease2 = newLease(anotherBand, customer2, "14.08.2016", Region.pardubicky, 3);
        leaseManager.createLease(lease2);
        assertNotNull(lease2.getId());
        assertNotNull(leaseManager.findLeaseById(lease1.getId()));
        assertNotNull(leaseManager.findLeaseById(lease2.getId()));
        
        Lease getLease1 = leaseManager.findLeaseById(lease1.getId());
        assertDeepEquals(lease1, getLease1);
        assertNotSame(lease1, getLease1);
        
        assertDeepNotEquals(lease1, lease2);
        
        Lease getLease2 = leaseManager.findLeaseById(lease2.getId());
        assertDeepEquals(lease2, getLease2);
        assertNotSame(lease2, getLease2);
    }

    @Test
    public void testUpdateLease() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        bandManager.createBand(band);
        
        Band band2 = newBand("Slize", styles, Region.slovensko, 200.00, 8.0);
        bandManager.createBand(band2);
        
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        customerManager.createCustomer(customer);
        
        Customer customer2 = newCustomer("Lenka si to cte :O", "Samozrejme :P 666", "Kartouzska 69");
        customerManager.createCustomer(customer2);
        
        
        Lease lease = newLease(band, customer, "01.02.2016", Region.jihocesky, 2);
        leaseManager.createLease(lease);
        Long leaseId = lease.getId();
        
        Lease lease2 = newLease(band, customer, "01.05.2016", Region.jihocesky, 8);
        leaseManager.createLease(lease2);
        
        
        DateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        
        // ---- Duration
        lease.setDuration(50);
        
        leaseManager.updateLease(lease);
        
        lease = leaseManager.findLeaseById(leaseId);
        
        assertEquals(band.getId(), lease.getBand().getId());
        assertEquals(customer.getId(), lease.getCustomer().getId());
        assertEquals(format.parse("01.02.2016"), lease.getDate());
        assertEquals(Region.jihocesky, lease.getPlace());
        assertEquals(50, lease.getDuration());
        
        
        // ---- Place
        lease.setPlace(Region.jihomoravsky);
        
        leaseManager.updateLease(lease);
        
        lease = leaseManager.findLeaseById(leaseId);
        
        assertEquals(band.getId(), lease.getBand().getId());
        assertEquals(customer.getId(), lease.getCustomer().getId());
        assertEquals(format.parse("01.02.2016"), lease.getDate());
        assertEquals(Region.jihomoravsky, lease.getPlace());
        assertEquals(50, lease.getDuration());
        
        
        // ---- Date
        Date date = format.parse("08.08.2016");
        lease.setDate(date);
        
        leaseManager.updateLease(lease);
        
        lease = leaseManager.findLeaseById(leaseId);
        
        assertEquals(band.getId(), lease.getBand().getId());
        assertEquals(customer.getId(), lease.getCustomer().getId());
        assertEquals(format.parse("08.08.2016"), lease.getDate());
        assertEquals(Region.jihomoravsky, lease.getPlace());
        assertEquals(50, lease.getDuration());
        
        
        // ---- Customer
        lease.setCustomer(customer2);
        
        leaseManager.updateLease(lease);
        
        lease = leaseManager.findLeaseById(leaseId);
        
        assertEquals(band.getId(), lease.getBand().getId());
        assertEquals(customer2.getId(), lease.getCustomer().getId());
        assertEquals(format.parse("08.08.2016"), lease.getDate());
        assertEquals(Region.jihomoravsky, lease.getPlace());
        assertEquals(50, lease.getDuration());
        
        
        // ---- Band
        lease.setBand(band2);
        
        leaseManager.updateLease(lease);
        
        lease = leaseManager.findLeaseById(leaseId);
        
        assertEquals(band2.getId(), lease.getBand().getId());
        assertEquals(customer2.getId(), lease.getCustomer().getId());
        assertEquals(format.parse("08.08.2016"), lease.getDate());
        assertEquals(Region.jihomoravsky, lease.getPlace());
        assertEquals(50, lease.getDuration());
        
        // --- 
        assertDeepEquals(lease2, leaseManager.findLeaseById(lease2.getId()));
    }

    @Test
    public void testDeleteLease() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        bandManager.createBand(band);
        
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        customerManager.createCustomer(customer);
        
        Lease lease = newLease(band, customer, "01.02.2016", Region.jihocesky, 2);
        leaseManager.createLease(lease);
        
        Lease lease2 = newLease(band, customer, "01.05.2016", Region.jihocesky, 8);
        leaseManager.createLease(lease2);
        
        
        assertNotNull(leaseManager.findLeaseById(lease.getId()));
        assertNotNull(leaseManager.findLeaseById(lease2.getId()));
        
        leaseManager.deleteLease(lease);

        assertNull(leaseManager.findLeaseById(lease.getId()));
        assertNotNull(leaseManager.findLeaseById(lease2.getId()));
    }

    @Test
    public void testFindLeaseById() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        bandManager.createBand(band);
        
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        customerManager.createCustomer(customer);
        
        Lease lease = newLease(band, customer, "01.02.2016", Region.jihocesky, 2);
        leaseManager.createLease(lease);
        Long leaseId = lease.getId();
        
        Lease result = leaseManager.findLeaseById(leaseId);
        assertDeepEquals(lease, result);
    }

    @Test
    public void testFindAllLeases() throws Exception {
        SimpleDateFormat sfd = new SimpleDateFormat("dd.mm.yyyy");
        
        Collection<Lease> allCustomers = leaseManager.findAllLeases();
        
        assertTrue(allCustomers.isEmpty());
        
        Customer cus1 = newCustomer("Igor Kosik", "+421 944 326 548", "Kernova 9");
        customerManager.createCustomer(cus1);
        List<Style> styles = new ArrayList<>();
        styles.add(Style.blues);
        styles.add(Style.reggae);
        Band band1 = newBand("A ty citas toto?", styles, Region.karlovarsky, 156.23, 6.1);
        bandManager.createBand(band1);
        
        Lease lease1 = newLease(band1, cus1, "01.12.2017", Region.kralovohradecky, 4);
        leaseManager.createLease(lease1);
        
        Customer cus2 = newCustomer("Frantisek Smutny", "236 155 885", "Most SNP 4.stlp");
        customerManager.createCustomer(cus2);
        Band band2 = newBand("HEX", styles, Region.slovensko, 421.00, 5.5);
        bandManager.createBand(band2);
        
        Lease lease2 = newLease(band2, cus2, "05.06.2016", Region.praha, 3);
        leaseManager.createLease(lease2);
        
        List<Lease> sample = new ArrayList<>();
        List<Lease> actual = new ArrayList<>();
        
        sample.add(lease1);
        sample.add(lease2);
        
        allCustomers = leaseManager.findAllLeases();
        actual.addAll(allCustomers);
        
        Collections.sort(sample, idComparator);
        Collections.sort(actual, idComparator);
        
        assertDeepEquals(sample, actual);
        
    }

    @Test
    public void testFindLeasesForBand() throws Exception {
        Customer customer1 = newCustomer("Tomas Oravec","012 345 789", "Brezno 123");
        customerManager.createCustomer(customer1);
        List<Style> styles = new ArrayList<>();
        styles.add(Style.dnb);
        styles.add(Style.classical);
        Band band1 = newBand("Ugly band", styles, Region.liberecky, 569.36, 1.1);
        bandManager.createBand(band1);

        Lease lease1 = newLease(band1, customer1, "31.02.2019", Region.jihocesky, 6);
        leaseManager.createLease(lease1);

        Customer customer2 = newCustomer("Ondrej Brezovec", "569 123 789", "Zilina 1020");
        customerManager.createCustomer(customer2);
        Band band2 = newBand("Nice band", styles, Region.karlovarsky, 222.66, 3.3);
        bandManager.createBand(band2);

        Lease lease2 = newLease(band2, customer2, "23.06.2017", Region.praha, 3);
        leaseManager.createLease(lease2);

        Customer customer3 = newCustomer("Mario Hubrak", "566 222 111", "Lucenec 183");
        customerManager.createCustomer(customer3);

        lease1=leaseManager.findLeaseById(lease1.getId());

        Lease lease3 = newLease(band1, customer3, "22.08.2016", Region.ustecky, 5);
        leaseManager.createLease(lease3);

        List<Lease> sample= new ArrayList<>();
        List<Lease> actual= new ArrayList<>();

        sample.add(lease1);
        sample.add(lease3);

        Collection<Lease> leasesForBand = leaseManager.findLeasesForBand(band1);
        actual.addAll(leasesForBand);

        Collections.sort(sample,idComparator);
        Collections.sort(actual, idComparator);

        assertDeepEquals(sample,actual);

        // band don't have lease
        Band band3 = newBand("Lonely band", styles, Region.olomoucky, 55.33, 2.3);
        bandManager.createBand(band3);
        leasesForBand = leaseManager.findLeasesForBand(band3);
        assertTrue(leasesForBand.isEmpty());

        //consistency
        Lease getLease = leaseManager.findLeaseById(lease2.getId());
        assertDeepEquals(lease2,getLease);

    }

    @Test(expected = NullPointerException.class)
    public void testFindLeasesForBandWithWrongArgument() throws Exception {
        Collection<Lease> leasesForBand = new ArrayList<>();

        leasesForBand=leaseManager.findLeasesForBand(null);
        
        List<Style> styles = new ArrayList<>();
        styles.add(Style.folk);
        styles.add(Style.disco);
        Band band1 = newBand("I drift off", styles, Region.stredocesky, 222.33, 6.6);
        
        leasesForBand = leaseManager.findLeasesForBand(band1);
    }

    @Test
    public void testFindLeasesForCustomer() throws Exception {
        Customer customer1 = newCustomer("Tomas Oravec","+421 944 222 222", "Brezno 123");
        customerManager.createCustomer(customer1);
        
        List<Style> styles = new ArrayList<>();
        styles.add(Style.pop);
        styles.add(Style.jazz);
        Band band1 = newBand("Nemozem povedat", styles, Region.slovensko, 123.45, 6.7);
        bandManager.createBand(band1);

        Lease lease1 = newLease(band1, customer1, "11.04.2016", Region.plzensky, 5);
        leaseManager.createLease(lease1);

        Customer customer2 = newCustomer("Ondrej Brezovec","+421 922 222 222", "Zilina 1020");
        customerManager.createCustomer(customer2);
        Band band2 = newBand("No name", styles, Region.slovensko, 100.22, 4.6);
        bandManager.createBand(band2);

        Lease lease2 = newLease(band2, customer2, "23.07.2017", Region.liberecky, 7);
        leaseManager.createLease(lease2);

        Band band3 = newBand("AC/DC", styles, Region.moravskosliezsky, 45.26, 8.9);
        bandManager.createBand(band3);

        Lease lease3 = newLease(band3, customer1, "15.03.2016", Region.pardubicky, 4);
        leaseManager.createLease(lease3);

        List<Lease> sample= new ArrayList<>();
        List<Lease> actual= new ArrayList<>();

        sample.add(lease1);
        sample.add(lease3);

        Collection<Lease> leasesForCustomer=leaseManager.findLeasesForCustomer(customer1);
        actual.addAll(leasesForCustomer);

        Collections.sort(sample,idComparator);
        Collections.sort(actual, idComparator);

        assertDeepEquals(sample,actual);

        // test with customer who doesn't have lease
        Customer customer3 = newCustomer("Andrej Pincik", "568 789 123", "Brezovec 10");
        customerManager.createCustomer(customer3);
        leasesForCustomer=leaseManager.findLeasesForCustomer(customer3);
        assertTrue(leasesForCustomer.isEmpty());

        //consistency
        Lease getLease = leaseManager.findLeaseById(lease2.getId());
        assertDeepEquals(lease2,getLease);
    }
    
    @Test(expected = NullPointerException.class)
    public void testFindLeasesForCustomerWithWrongArgument() throws Exception {
        Collection<Lease> leasesForCustomer = new ArrayList<>();    
        
        leasesForCustomer = leaseManager.findLeasesForCustomer(null);
            
        //customer isn't in DB
        Customer customer2 = newCustomer("Tomas Mician","065 482 266", "Cadca 123");
        leasesForCustomer = leaseManager.findLeasesForCustomer(customer2); 
    }
    
    /**
     * 
     * @param name
     * @param styles
     * @param region
     * @param price
     * @param rate
     * @return 
     */
    private static Band newBand(String name, List<Style> styles, Region region, Double price, Double rate) {
        Band band = new Band();
        band.setBandName(name);
        band.setStyles(styles);
        band.setRegion(region);
        band.setPricePerHour(price);
        band.setRate(rate);
        
        return band;
    }
    
    public Customer newCustomer(String name, String phoneNumber, String address){
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);

        return customer;
    }

    private Lease newLease(Band band, Customer customer, String sDate, Region region, int duration) throws ParseException {
        Lease lease = new Lease();
        lease.setBand(band);
        lease.setCustomer(customer);
        
        DateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        Date date = format.parse(sDate);
        lease.setDate(date);
        
        lease.setPlace(region);
        lease.setDuration(duration);
        return lease;
    }

    private void assertDeepEquals(Lease expected, Lease actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBand().getId(), actual.getBand().getId());
        assertEquals(expected.getCustomer().getId(), actual.getCustomer().getId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getPlace(), actual.getPlace());
        assertEquals(expected.getDuration(), actual.getDuration());
    }
    
    private void assertDeepNotEquals(Lease expected, Lease actual) {
        boolean res = true;
        
        if(!Objects.equals(expected.getId(), actual.getId())) res = false;
        if(!Objects.equals(expected.getBand().getId(), actual.getBand().getId())) res = false;
        if(!Objects.equals(expected.getCustomer().getId(), actual.getCustomer().getId())) res = false;
        if(!expected.getDate().equals(actual.getDate())) res = false;
        if(!expected.getPlace().equals(actual.getPlace())) res = false;
        if(expected.getDuration() != expected.getDuration()) res = false;
        
        assertFalse(res);
    }

    private void assertDeepEquals(List<Lease> sample, List<Lease> actual) {
        for(int i = 0; i < sample.size(); i++) {
            Lease first = sample.get(i);
            Lease second = actual.get(i);
            assertDeepEquals(first, second);
        }
    }
    
    private static Comparator<Lease> idComparator = new Comparator<Lease>() {

        @Override
        public int compare(Lease o1, Lease o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}