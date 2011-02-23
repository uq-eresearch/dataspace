package net.metadata.dataspace.auth.impl;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.auth.AuthenticationManager;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.access.SourceDao;
import net.metadata.dataspace.data.access.UserDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.record.User;
import net.metadata.dataspace.data.model.types.AgentType;
import net.metadata.dataspace.data.model.version.AgentVersion;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;

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
            //TODO move this to spring configurations so it is turned off for production
            if (userName.equals("test") && password.equals("test")) {
                UserDao userDao = RegistryApplication.getApplicationContext().getDaoManager().getUserDao();
                User user = userDao.getByUsername(userName);
                if (user == null) {
                    user = new User(userName);
                    userDao.save(user);
                }
                request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, user);

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

                        UserDao userDao = RegistryApplication.getApplicationContext().getDaoManager().getUserDao();
                        User user = userDao.getByUsername(userName);
                        if (user == null) {
                            user = new User(userName);
                            userDao.save(user);
                        }
                        createLoggedInAgent(namingEnum);
                        request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, user);
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
        request.setAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER, null);
        return ProviderHelper.createErrorResponse(new Abdera(), 200, Constants.HTTP_STATUS_200);
    }

    private void createLoggedInAgent(NamingEnumeration namingEnum) throws NamingException {
        AgentDao agentDao = RegistryApplication.getApplicationContext().getDaoManager().getAgentDao();
        SourceDao sourceDao = RegistryApplication.getApplicationContext().getDaoManager().getSourceDao();
        Map<String, String> attributesMap = getAttributes(namingEnum);
        String uqMail = attributesMap.get("uqmail");
        if (agentDao.getByEmail(uqMail) == null) {
            EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
            EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();

            Agent agent = ((Agent) entityCreator.getNextRecord(Agent.class));
            AgentVersion version = ((AgentVersion) entityCreator.getNextVersion(agent));
            Source systemSource = sourceDao.getBySourceURI(Constants.UQ_SOURCE_URI);
            transaction.begin();
            String name = attributesMap.get("cn");
            version.setTitle(name);
            String description = attributesMap.get("title");
            version.setDescription(description);
            Date now = new Date();
            version.setUpdated(now);
            Set<String> authors = new HashSet<String>();
            authors.add("The University of Queensland DataSpace");
            version.setAuthors(authors);
            version.setType(AgentType.PERSON);
            version.setMbox(uqMail);
            version.setAlternative(attributesMap.get("pub-displayname"));

            version.setParent(agent);
            agent.getVersions().add(version);
            version.getParent().setPublished(version);
            agent.setUpdated(now);

            agent.setUpdated(now);
            agent.setLocatedOn(systemSource);
            agent.setSource(systemSource);
            agent.setPublisher(agent);
            agent.getCreators().add(agent);


            entityManager.persist(version);
            entityManager.persist(agent);
            transaction.commit();
        }
    }

    private Map<String, String> getAttributes(NamingEnumeration namingEnum) throws NamingException {
        Map<String, String> attributes = new HashMap<String, String>();
        if (namingEnum.hasMore()) {
            for (NamingEnumeration attrs = ((SearchResult) namingEnum.next()).getAttributes().getAll(); attrs.hasMore();) {
                Attribute attr = (Attribute) attrs.next();
                String id = attr.getID();
                NamingEnumeration values = attr.getAll();
                String result = "";
                while (values.hasMore()) {
                    result = result + values.next() + " ";
                }
                attributes.put(id, result.trim());
            }
        }
        return attributes;
    }
}
