package cz.muni.fi.pv168.bandsproject;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Created by Lenka on 9.3.2016.
 */
public class CustomerManagerImpl implements CustomerManager {
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public CustomerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public void createCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if (customer.getId() != null) {
            throw new IllegalArgumentException("customer id is already set");
        }

        SimpleJdbcInsert insertCustomer = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("customer").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("name", customer.getName());
        parameters.put("phoneNumber", customer.getPhoneNumber());
        parameters.put("address", customer.getAddress());
        Number id = insertCustomer.executeAndReturnKey(parameters);
        customer.setId(id.longValue());
    }

    @Override
    public void updateCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if(customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        String SQL = "UPDATE CUSTOMER SET name = ?,phoneNumber = ?,address = ? WHERE id = ?";
        jdbcTemplateObject.update(SQL, customer.getName(), customer.getPhoneNumber(), customer.getAddress(), customer.getId());
    }

    @Override
    public void deleteCustomer(Customer customer) throws ServiceFailureException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        jdbcTemplateObject.update("DELETE FROM CUSTOMER WHERE id = ?", customer.getId());
    }
    
    @Override
    public List<Customer> getAllCustomers() {
        try {
            List<Customer> customers = jdbcTemplateObject.query("select * from customer", customerMapper);
            return customers;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Customer getCustomer(Long id) {
        try {
            Customer customer = jdbcTemplateObject.queryForObject("SELECT * FROM CUSTOMER WHERE id = ?", customerMapper, id);
            return customer;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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

    private RowMapper<Customer> customerMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setPhoneNumber(rs.getString("phoneNumber"));
            customer.setAddress(rs.getString("address"));
            return customer;
        }
    };
}
