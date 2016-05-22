package cz.muni.fi.pv168.bandsproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import javax.sql.DataSource;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;


/**
* Spring Java configuration class. See http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java
*
* @author Martin Kuba makub@ics.muni.cz
*/

//import org.apache.derby.jdbc.ClientDriver
@Configuration  //je to konfigurace pro Spring
@EnableTransactionManagement //bude ?�dit transakce u metod ozna?en�ch @Transactional
public class SpringConfig {
    private final static Logger log = LoggerFactory.getLogger(SpringConfig.class);

    Collection collection = null;

    public SpringConfig() throws Exception {
        try {
            DBUtils.dropDatabaseCollection();
            log.info("dropDatabaseCollection SUCCESS");
        }
        catch (Exception ex) {
            log.error("dropDatabaseCollection FAIL");
            throw ex;
        }
        collection = DBUtils.loadOrCreateDatabaseCollection();
    }

    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .setName("bandDB")
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
        DBUtilsLease.createIfNotExistsLeaseResource();
        return new LeaseManagerImpl(collection);
    }
}
