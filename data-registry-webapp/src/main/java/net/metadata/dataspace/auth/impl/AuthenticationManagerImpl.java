package net.metadata.dataspace.auth.impl;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.auth.util.LDAPUtil;
import net.metadata.dataspace.data.access.UserDao;
import net.metadata.dataspace.data.model.record.User;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 3:00:50 PM
 */
public class AuthenticationManagerImpl implements AuthenticationManager {
    private Logger logger = Logger.getLogger(getClass());
    private Map<String, DirContext> userDirContexts = new HashMap<String, DirContext>();

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
            //TODO move this to spring configurations so it is turned off for production
            if (userName.equals("test") && password.equals("test")) {
                UserDao userDao = RegistryApplication.getApplicationContext().getDaoManager().getUserDao();
                User user = userDao.getByUsername(userName);
                if (user == null) {
                    user = new User(userName, "Test", "test@uq.edu.au");
                    userDao.save(user);
                }
                request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, user);
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, "ldaps://ldap.uq.edu.au");
                SearchControls ctls = new SearchControls();
                ctls.setReturningObjFlag(true);
                ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                try {
                    DirContext ctx = new InitialDirContext(env);
                    setDirContext(ctx, user);
                } catch (NamingException e) {
                    logger.warn("Could not set LDAP context for user " + userName);
                }
                logger.info("Authenticated user: " + userName);
                return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
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

                        NamingEnumeration namingEnum = ctx.search("ou=staff,ou=people,o=the university of queensland,c=au", "(uid=" + userName + ")", ctls);
                        Map<String, String> attributesMap = LDAPUtil.getAttributesAsMap(namingEnum);

                        UserDao userDao = RegistryApplication.getApplicationContext().getDaoManager().getUserDao();
                        User user = userDao.getByUsername(userName);
                        if (user == null) {
                            user = new User(userName);
                            String email = attributesMap.get("mail");
                            String name = attributesMap.get("cn");
                            if (email == null) {
                                email = userName + "@uq.edu.au";
                            }
                            user.setEmail(email);
                            user.setDisplayName(name);
                            userDao.save(user);
                        }
                        request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, user);
                        setDirContext(ctx, user);
                        logger.info("Authenticated user: " + userName);
                        return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
                    } else {
                        String message = "Authentication Failed, User not found";
                        logger.warn(message);
                        return ProviderHelper.badrequest(request, message);
                    }
                } catch (NamingException e) {
                    String message = "Authentication Failed: " + e.getMessage();
                    logger.warn(message);
                    return ProviderHelper.badrequest(request, message);
                }
            }
        }

    }

    @Override
    public ResponseContext logout(RequestContext request) {
        User user = (User) request.getAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER);
        userDirContexts.remove(user.getEmail());
        request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, null);
        return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
    }

    @Override
    public DirContext getDirContext(User currentUser) {
        if (currentUser == null) {
            return null;
        } else {
            return userDirContexts.get(currentUser.getEmail());
        }
    }

    @Override
    public void setDirContext(DirContext ctx, User currentUser) {
        userDirContexts.put(currentUser.getEmail(), ctx);
    }
}
