package cz.muni.fi.pv168.bandsproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.xmldb.api.base.Collection;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 * Spring config for tests. See
 * <ul>
 *  <li><a href="http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java">Java-based container configuration</a></li>
 *  <li><a href="http://static.springsource.org/spring/docs/current/spring-framework-reference/html/testing.html#testcontext-tx">Testing - Transaction management</a></li>
 *  <li><a href="http://static.springsource.org/spring/docs/current/spring-framework-reference/html/jdbc.html#jdbc-embedded-database-dao-testing">Testing data access logic with an embedded database</a></li>
 * </ul>
 */
/**
 * Created by Lenka on 17.4.2016.
 */
@Configuration
@EnableTransactionManagement
public class MySpringTestConfig {
    private final static Logger log = LoggerFactory.getLogger(MySpringTestConfig.class);

    Collection collection = null;

    public MySpringTestConfig() throws Exception {
        try {
            DBUtils.dropDatabaseCollection();
            log.info("dropDatabaseCollection SUCCESS");
        }
        catch (Exception ex) {
            log.info("dropDatabaseCollection FAIL");
            throw ex;
        }
        collection = DBUtils.loadOrCreateDatabaseCollection();
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .addScript("classpath:band-schema.sql")
                .addScript("classpath:fill-table.sql")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public CustomerManager customerManager() throws Exception {
        DBUtils.createIfNotExistsCustomerResource();
        return new CustomerManagerImpl(collection);
    }

    @Bean
    public BandManager bandManager() throws Exception {
        DBUtilsBand.createIfNotExistsBandResource();
        return new BandManagerImpl(collection);
    }

    @Bean
    public LeaseManager leaseManager() throws Exception {
        LeaseManagerImpl leaseManager = new LeaseManagerImpl(dataSource());
        leaseManager.setBandManager(bandManager());
        leaseManager.setCustomerManager(customerManager());
        return leaseManager;
    }
}
