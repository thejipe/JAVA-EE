package InitApp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

public class InitBanqueWeb implements ServletContextListener {

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
