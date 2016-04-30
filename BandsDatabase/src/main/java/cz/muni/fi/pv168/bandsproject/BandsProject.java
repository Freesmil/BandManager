package cz.muni.fi.pv168.bandsproject;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.List;
/**
 *
 * @author Lenka
 */
public class BandsProject {
/*
    final static Logger log = LoggerFactory.getLogger(BandsProject.class);

    public static DataSource createMemoryDatabase() {
        BasicDataSource bds = new BasicDataSource();
        //set JDBC driver and URL
        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl("jdbc:derby:memory:bandsDB;create=true");
        //populate db with tables and data
        new ResourceDatabasePopulator(
                new ClassPathResource("schema-javadb.sql"),
                new ClassPathResource("test-data.sql"))
                .execute(bds);
        return bds;
    }

    public static void main(String[] args) throws BandException {

        log.info("zaciname");

        DataSource dataSource = createMemoryDatabase();
        BandManager bandManager = new BandManagerImpl(dataSource);

        List<Band> allBands = bandManager.getAllBands();
        System.out.println("allBands = " + allBands);

    }
*/
}