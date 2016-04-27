package cz.muni.fi.pv168.bandsproject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
    public CustomerManager customerManager() {
        return new CustomerManagerImpl(dataSource());
    }

    @Bean
    public BandManager bandManager() {
        // BookManagerImpl nepoužívá Spring JDBC, musíme mu vnutit spolupráci se Spring transakcemi !
        return new BandManagerImpl(new TransactionAwareDataSourceProxy(dataSource()));
    }

    @Bean
    public LeaseManager leaseManager() {
        LeaseManagerImpl leaseManager = new LeaseManagerImpl(dataSource());
        leaseManager.setBandManager(bandManager());
        leaseManager.setCustomerManager(customerManager());
        return leaseManager;
    }
}
