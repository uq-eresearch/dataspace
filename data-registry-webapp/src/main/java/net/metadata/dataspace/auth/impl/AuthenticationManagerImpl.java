package net.metadata.dataspace.auth.impl;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.data.access.UserDao;
import net.metadata.dataspace.data.model.base.User;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 3:00:50 PM
 */
public class AuthenticationManagerImpl implements AuthenticationManager {
    private Logger logger = Logger.getLogger(getClass());

    @Override
    public User getCurrentUser(RequestContext request) {
        User user = (User) request.getAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER);
        return user;
    }

    @Override
    public ResponseContext login(RequestContext request) {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        if (userName == null || password == null) {
            return ProviderHelper.badrequest(request, "Username and password missing");
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
/*
                    NamingEnumeration namingEnum = ctx.search("ou=staff,ou=people,o=the university of queensland,c=au", "(uid=" + userName + ")", ctls);
                    if (namingEnum.hasMore()) {
                        for (NamingEnumeration attrs = ((SearchResult) namingEnum.next()).getAttributes().getAll(); attrs.hasMore();) {
                            Attribute attr = (Attribute) attrs.next();
                            System.out.println("attribute: " + attr.getID());

                            *//* print each value *//*
                            for (NamingEnumeration e = attr.getAll(); e.hasMore(); System.out.println("\tvalue: " + e.next()))
                                ;
                        }
                    }
*/
                    //TODO how do users get into our system? DO we add them when they authenticate here?
                    UserDao userDao = RegistryApplication.getApplicationContext().getDaoManager().getUserDao();
                    User user = userDao.getByUsername(userName);
                    if (user == null) {
                        user = new User(userName);
                        userDao.save(user);
                    }
                    request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, user);
//                            request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_LDAP_CONTEXT, ctx);

                    logger.info("Authenticated user: " + userName);
                    return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
                } else {
                    String message = "Authentication Failed, User not found";
                    logger.warn(message);
                    return ProviderHelper.notfound(request, message);
                }
            } catch (NamingException e) {
                String message = "Authentication Failed: " + e.getMessage();
                logger.warn(message);
                return ProviderHelper.badrequest(request, message);
            }
        }

    }

    @Override
    public ResponseContext logout(RequestContext request) {
        request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, null);
        return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
    }
}
