package cz.muni.fi.pv168.bandsproject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Created by Lenka on 9.3.2016.
 */
public class LeaseManagerImpl implements LeaseManager{
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    private BandManager bandManager;
    private CustomerManager customerManager;

    public LeaseManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }


    public void setBandManager(BandManager bandManager) {
        this.bandManager = bandManager;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    @Override 
    public void createLease(Lease lease) throws ServiceFailureException {
        validate(lease);
        if (lease.getId() != null) {
            throw new IllegalArgumentException("Lease id is already set");
        }
        SimpleJdbcInsert insertLease = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("lease").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("idBand", lease.getBand().getId());
        parameters.put("idCustomer", lease.getCustomer().getId());
        parameters.put("date", lease.getDate());
        parameters.put("region", lease.getPlace().ordinal());
        parameters.put("duration", lease.getDuration());
        Number id = insertLease.executeAndReturnKey(parameters);
        lease.setId(id.longValue());
    }

    @Override
    public void updateLease(Lease lease) throws ServiceFailureException {
        validate(lease);
        if(lease.getId() == null) {
            throw new IllegalArgumentException("Lease id is null");
        }
        String SQL = "UPDATE lease SET idBand = ?,idCustomer = ?,date = ?,region = ?,duration = ? WHERE id = ?";
        jdbcTemplateObject.update(SQL, lease.getBand().getId(), lease.getCustomer().getId(), lease.getDate(),
        lease.getPlace().ordinal(), lease.getDuration(), lease.getId());
    }

    @Override
    public void deleteLease(Lease lease) throws ServiceFailureException {
        if (lease == null) {
            throw new IllegalArgumentException("lease is null");
        }
        if (lease.getId() == null) {
            throw new IllegalArgumentException("lease id is null");
        }
        jdbcTemplateObject.update("DELETE FROM lease WHERE id = ?", lease.getId());
    }

    @Override
    public Lease findLeaseById(Long id) {
        try {
            Lease lease = jdbcTemplateObject.queryForObject("SELECT * FROM lease WHERE id = ?", leaseMapper, id);
            return lease;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Lease> findAllLeases() {
        try {
            List <Lease> leases = jdbcTemplateObject.query("SELECT * FROM lease", leaseMapper);
            return leases;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Lease> findLeasesForBand(Band band) {
        try {
            List <Lease> leases = jdbcTemplateObject.query("SELECT * FROM lease WHERE idBand = ?", leaseMapper, band.getId());
            return leases;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Lease> findLeasesForCustomer(Customer customer) {
        try {
            List <Lease> leases = jdbcTemplateObject.query("SELECT * FROM lease WHERE idCustomer = ?", leaseMapper, customer.getId());
            return leases;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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

    private RowMapper<Lease> leaseMapper = new RowMapper<Lease>(){
        @Override
        public Lease mapRow(ResultSet rs, int rowNum) throws SQLException {
            Lease lease = new Lease();
            lease.setId(rs.getLong("id"));
            lease.setBand(new BandManagerImpl(dataSource).findBandById(rs.getLong("idBand")));
            lease.setCustomer(new CustomerManagerImpl(dataSource).getCustomer(rs.getLong("idCustomer")));
            lease.setDate(rs.getTimestamp("date"));
            lease.setPlace(Region.values()[rs.getInt("region")]);
            lease.setDuration(rs.getInt("duration"));
            return lease;
        }
    };
}
