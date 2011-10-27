package net.metadata.dataspace.auth.impl;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.atom.util.AdapterInputHelper;
import net.metadata.dataspace.atom.util.OperationHelper;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.data.access.UserDao;
import net.metadata.dataspace.data.access.manager.DaoManager;
import net.metadata.dataspace.data.model.record.User;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.*;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 3:00:50 PM
 */
@Transactional
public class AuthenticationManagerImpl implements AuthenticationManager {
    private Logger logger = Logger.getLogger(getClass());
    private Properties defaultUsersProperties;
    private Map<String, String[]> defaultUsers = new HashMap<String, String[]>();
    private DaoManager daoManager;

    @Override
    public User getCurrentUser(RequestContext request) {
        User user = (User) request.getAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER);
        return user;
    }

    @Override
    public ResponseContext login(RequestContext request) {
        // If HEAD request, just send 200 OK
        if (request.getMethod().equals("HEAD")) {
        	return OperationHelper.createResponse(200, "", request.getHeader("Host"));
        }
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        if (userName == null || password == null) {
            return OperationHelper.createResponse(400, "Username and password missing", request.getHeader("Host"));
        } else {
            if (defaultUsers.containsKey(userName) && defaultUsers.get(userName)[1].equals(password)) {
                UserDao userDao = getDaoManager().getUserDao();
                User user = userDao.getByUsername(userName);
                if (user == null) {
                    user = new User(userName, defaultUsers.get(userName)[3], defaultUsers.get(userName)[2]);
                    userDao.save(user);
                }
                request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, user);
                Hashtable<String, String> env = new Hashtable<String, String>();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, "ldaps://ldap.uq.edu.au");
                SearchControls ctls = new SearchControls();
                ctls.setReturningObjFlag(true);
                ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                logger.info("Authenticated user: " + userName);
                return OperationHelper.createResponse(200, Constants.HTTP_STATUS_200, request.getHeader("Host"));
            } else {
                try {
                    Hashtable<String, String> env = new Hashtable<String, String>();
                    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                    env.put(Context.PROVIDER_URL, "ldaps://ldap.uq.edu.au");
                    SearchControls ctls = new SearchControls();
                    ctls.setReturningObjFlag(true);
                    ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                    DirContext ctx = new InitialDirContext(env);
                    NamingEnumeration<SearchResult> answer = ctx.search("ou=staff,ou=people,o=the university of queensland,c=au", "(uid=" + userName + ")", ctls);
                    if (answer.hasMore()) {
                        SearchResult sr = (SearchResult) answer.next();
                        String dn = sr.getNameInNamespace();
                        env.put(Context.SECURITY_AUTHENTICATION, "simple");
                        env.put(Context.SECURITY_PRINCIPAL, dn);
                        env.put(Context.SECURITY_CREDENTIALS, password);
                        ctx = new InitialDirContext(env);

                        NamingEnumeration<SearchResult> namingEnum = ctx.search("ou=staff,ou=people,o=the university of queensland,c=au", "(uid=" + userName + ")", ctls);
                        Map<String, String> attributesMap = getAttributesAsMap(namingEnum);

                        UserDao userDao = getDaoManager().getUserDao();
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
                        logger.info("Authenticated user: " + userName);
                        return OperationHelper.createResponse(200, Constants.HTTP_STATUS_200, request.getHeader("Host"));
                    } else {
                        String message = "Authentication Failed, User not found";
                        logger.warn(message);
                        return OperationHelper.createResponse(400, message, request.getHeader("Host"));
                    }
                } catch (NamingException e) {
                    String message = "Authentication Failed: " + e.getMessage();
                    logger.warn(message);
                    return OperationHelper.createResponse(400, message, request.getHeader("Host"));
                }
            }
        }

    }


    private Map<String, String> getAttributesAsMap(NamingEnumeration<SearchResult> namingEnum) throws NamingException {
        Map<String, String> attributes = new HashMap<String, String>();
        if (namingEnum.hasMore()) {
            for (NamingEnumeration<? extends Attribute> attrs = namingEnum.next().getAttributes().getAll(); attrs.hasMore(); ) {
                Attribute attr = (Attribute) attrs.next();
                String id = attr.getID();
                NamingEnumeration<?> values = attr.getAll();
                String result = "";
                while (values.hasMore()) {
                    result = result + values.next() + " ";
                }
                attributes.put(id, result.trim());
            }
        }
        return attributes;
    }

    @Override
    public ResponseContext logout(RequestContext request) {
        request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, null);
        return OperationHelper.createResponse(200, Constants.HTTP_STATUS_200);
    }

    public void setDefaultUsersProperties(Properties defaultUsersProperties) {
        this.defaultUsersProperties = defaultUsersProperties;
        Collection<Object> defaultUsersString = defaultUsersProperties.values();
        for (Object userString : defaultUsersString) {
            String[] split = userString.toString().split(",");
            defaultUsers.put(split[0], split);
        }
    }

    public Properties getDefaultUsersProperties() {
        return defaultUsersProperties;
    }

	public DaoManager getDaoManager() {
		return daoManager;
	}

	public void setDaoManager(DaoManager daoManager) {
		this.daoManager = daoManager;
	}

}
