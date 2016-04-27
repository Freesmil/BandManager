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
* Spring Java configuration class. See http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java
*
* @author Martin Kuba makub@ics.muni.cz
*/

//import org.apache.derby.jdbc.ClientDriver
@Configuration  //je to konfigurace pro Spring
@EnableTransactionManagement //bude ?�dit transakce u metod ozna?en�ch @Transactional
public class SpringConfig {

    @Bean
    public DataSource dataSource(){
        /*//s�tov� datab�ze
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
        bds.setUrl("jdbc:derby://localhost:1527/bandDB");
        //bds.setUsername("admin");
        //bds.setPassword("admin");
        return bds;*/

        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .setName("bandDB")
                .addScript("classpath:band-schema.sql")
                .addScript("classpath:fill-table.sql")
                .build();

    }

    @Bean //pot?eba pro @EnableTransactionManagement
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean //n� manager, bude obalen ?�zen�m transakc�
    public CustomerManager customerManager() {
        return new CustomerManagerImpl(dataSource());
    }

    @Bean
    public BandManager bandManager() {
        return new BandManagerImpl(new TransactionAwareDataSourceProxy(dataSource()));
    }

    @Bean
    public LeaseManager leaseManager() {
        return new LeaseManagerImpl(dataSource());
    }
}
