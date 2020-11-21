package InitApp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

/**
 * This class provide method to initialize the application when it's start.
 */
public class InitBanqueWeb implements ServletContextListener {

    /**
     * Override the contextInitalized method of ServletContextListener.
     * Register the data source, that will be used to exchange with the database, as attribut of the context.
     * @param sce {@link ServletContextEvent} that provide the servlet context.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var servletContext = sce.getServletContext();
        String nomDs = servletContext.getInitParameter("jdbc/Banque");
        try {
            var dataSource= (DataSource) new InitialContext().lookup("java:comp/env/" + nomDs);
            servletContext.setAttribute("dataSource", dataSource);
        } catch (NamingException e) {
            System.out.println("NamingException " + e.getMessage());
        }
    }
}