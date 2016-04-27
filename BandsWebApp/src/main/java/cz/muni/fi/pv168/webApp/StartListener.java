package cz.muni.fi.pv168.webApp;

import cz.muni.fi.pv168.bandsproject.BandManager;
import cz.muni.fi.pv168.bandsproject.CustomerManager;
import cz.muni.fi.pv168.bandsproject.LeaseManager;
import cz.muni.fi.pv168.bandsproject.SpringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
/**
 *
 * @author Lenka
 */
@WebListener
public class StartListener implements ServletContextListener {

    private final static Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        log.info("webova aplikace inicializovana");
        ServletContext servletContext = ev.getServletContext();
        ApplicationContext springContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        servletContext.setAttribute("customerManager", springContext.getBean("customerManager", CustomerManager.class));
        servletContext.setAttribute("bandManager", springContext.getBean("bandManager", BandManager.class));
        servletContext.setAttribute("leaseManager", springContext.getBean("leaseManager", LeaseManager.class));
        log.info("vytvoreny manazery a ulozeny do atributu servletContextu");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        log.info("aplikace konci");
    }
}