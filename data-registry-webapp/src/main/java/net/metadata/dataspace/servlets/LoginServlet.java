package net.metadata.dataspace.servlets;

import net.metadata.dataspace.app.DataRegistryApplication;
import net.metadata.dataspace.data.access.UserDao;
import net.metadata.dataspace.data.model.User;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 11:46:33 AM
 */
public class LoginServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("username");
        String password = req.getParameter("password");
        if (userName == null || password == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username and password missing");
        } else {
            try {
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, "ldaps://ldap.uq.edu.au");
                SearchControls ctls = new SearchControls();
                ctls.setReturningObjFlag(true);
                ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                DirContext ctx = new InitialDirContext(env);
                NamingEnumeration answer = ctx.search("ou=staff,ou=people,o=the university of queensland,c=au", "(uid=" + userName + ")", ctls);
                if (answer.hasMore()) {
                    SearchResult sr = (SearchResult) answer.next();
                    String dn = sr.getNameInNamespace();
                    env.put(Context.SECURITY_AUTHENTICATION, "simple");
                    env.put(Context.SECURITY_PRINCIPAL, dn);
                    env.put(Context.SECURITY_CREDENTIALS, password);
                    ctx = new InitialDirContext(env);

                    //TODO how do users get into our system? DO we add them when they authenticate here?
                    UserDao userDao = DataRegistryApplication.getApplicationContext().getDaoManager().getUserDao();
                    User user = userDao.getByUsername(userName);
                    if (user == null) {
                        user = new User(userName);
                        userDao.save(user);
                    }
                    req.setAttribute("currentUser", user);

                    logger.info("Authenticated user: " + sr.getName());
                    logger.info("Context: " + ctx.getNameInNamespace());
                } else {
                    logger.info("User not found");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NamingException e) {
                logger.warn("Could not authenticate user");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
