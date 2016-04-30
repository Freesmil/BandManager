package cz.muni.fi.pv168.bandsproject;

import java.util.List;

/**
 * Created by Lenka on 9.3.2016.
 */
public interface LeaseManager {

    /**
     *
     * @param lease
     */
    public void createLease(Lease lease);

    /**
     *
     * @param lease
     */
    public void updateLease(Lease lease);

    public void deleteLease(Lease lease);

    public Lease findLeaseById(Long id);

    public List<Lease> findAllLeases();

    public List<Lease> findLeasesForCustomer(Customer customer);

    public List<Lease> findLeasesForBand(Band band);
}
