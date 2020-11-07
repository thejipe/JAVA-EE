import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.util.EventListener;

public class InitBanqueWeb implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var appContext = sce.getServletContext();
        String nomDs = appContext.getInitParameter("jdbc/Banque");
        try {
            var initialContext = new InitialContext();
            var dataSource= (DataSource) initialContext.lookup("java:comp/env/" + nomDs);
            appContext.setAttribute("dataSource", dataSource);
        } catch (NamingException e) {
            System.out.println(e);
        }
    }
}
