package net.metadata.dataspace.auth.util;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.RegistryApplication;
import net.metadata.dataspace.data.access.AgentDao;
import net.metadata.dataspace.data.access.SourceDao;
import net.metadata.dataspace.data.access.manager.EntityCreator;
import net.metadata.dataspace.data.model.context.FullName;
import net.metadata.dataspace.data.model.context.Source;
import net.metadata.dataspace.data.model.record.Agent;
import net.metadata.dataspace.data.model.types.AgentType;
import net.metadata.dataspace.data.model.version.AgentVersion;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Author: alabri
 * Date: 25/02/2011
 * Time: 4:11:45 PM
 */
public class LDAPUtil {

    private final static Logger logger = Logger.getLogger(LDAPUtil.class);

    public static NamingEnumeration searchLDAPByEmail(String email) {
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldaps://ldap.uq.edu.au");
            SearchControls ctls = new SearchControls();
            ctls.setReturningObjFlag(true);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            DirContext ctx = new InitialDirContext(env);
            NamingEnumeration answer = ctx.search("ou=staff,ou=people,o=the university of queensland,c=au", "(mail=" + email + ")", ctls);
            return answer;
        } catch (NamingException e) {
            String message = "Agent not found in LDAP";
            logger.warn(message);
            return null;
        }
    }


    public static void createLoggedInAgent(Map<String, String> attributesMap) throws NamingException {
        AgentDao agentDao = RegistryApplication.getApplicationContext().getDaoManager().getAgentDao();
        String mail = attributesMap.get("mail");
        if (agentDao.getByEmail(mail) == null) {
            EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
            EntityManager entityManager = RegistryApplication.getApplicationContext().getDaoManager().getJpaConnnector().getEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();

            Agent agent = ((Agent) entityCreator.getNextRecord(Agent.class));
            AgentVersion version = ((AgentVersion) entityCreator.getNextVersion(agent));
            SourceDao sourceDao = RegistryApplication.getApplicationContext().getDaoManager().getSourceDao();
            Source systemSource = sourceDao.getBySourceURI(Constants.UQ_REGISTRY_URI_PREFIX);
            transaction.begin();
            if (systemSource == null) {
                systemSource = entityCreator.getNextSource();
                systemSource.setTitle(Constants.UQ_REGISTRY_TITLE);
                systemSource.setSourceURI(Constants.UQ_REGISTRY_URI_PREFIX);
                systemSource.setUpdated(new Date());
            }

            //always available attributes
            String name = attributesMap.get("cn");
            String organisation = attributesMap.get("ou");

            //attributes available when logged in
            String title = attributesMap.get("personalTitle");
            String givenName = attributesMap.get("givenName");
            String familyName = attributesMap.get("sn");
            if (title != null && givenName != null & familyName != null) {
                FullName fullName = entityCreator.getFullName();
                fullName.setTitle(title);
                fullName.setGivenName(givenName);
                fullName.setFamilyName(familyName);
                agent.setFullName(fullName);
            }
            String jobTitle = attributesMap.get("title");
            if (jobTitle == null) {
                jobTitle = "Staff Member";
            }
            String page = attributesMap.get("labeledURI");
            if (page != null) {
                version.getPages().add(page);
            }
            String nickName = attributesMap.get("pub-displayname");
            if (nickName != null) {
                version.getAlternatives().add(nickName);
            }
            version.setTitle(name);
            version.setDescription(name + " is a " + jobTitle + " in " + organisation + " at The University of Queensland");

            Date now = new Date();
            version.setUpdated(now);
            version.setType(AgentType.PERSON);
            version.getMboxes().add(mail);

            version.setParent(agent);
            agent.getVersions().add(version);
            version.getParent().setPublished(version);
            agent.setUpdated(now);
            agent.setPublishDate(now);
//            agent.setLicense("UQ License here");
//            agent.setRights("UQ Rights here");
            agent.setSource(systemSource);
            agent.getAuthors().add(agent);

            entityManager.persist(version);
            entityManager.persist(agent);
            transaction.commit();
        }
    }

    public static Map<String, String> getAttributesAsMap(NamingEnumeration namingEnum) throws NamingException {
        Map<String, String> attributes = new HashMap<String, String>();
        if (namingEnum.hasMore()) {
            for (NamingEnumeration attrs = ((SearchResult) namingEnum.next()).getAttributes().getAll(); attrs.hasMore(); ) {
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

    public static Agent createAgent(Map<String, String> attributesMap) {
        AgentDao agentDao = RegistryApplication.getApplicationContext().getDaoManager().getAgentDao();
        String mail = attributesMap.get("mail");
        Agent agent = agentDao.getByEmail(mail);
        if (agent == null) {
            EntityCreator entityCreator = RegistryApplication.getApplicationContext().getEntityCreator();
            agent = ((Agent) entityCreator.getNextRecord(Agent.class));
            AgentVersion version = ((AgentVersion) entityCreator.getNextVersion(agent));
            SourceDao sourceDao = RegistryApplication.getApplicationContext().getDaoManager().getSourceDao();
            Source systemSource = sourceDao.getBySourceURI(Constants.UQ_REGISTRY_URI_PREFIX);
            //always available attributes
            String name = attributesMap.get("cn");
            String organisation = attributesMap.get("ou");

            //attributes available when logged in
            String title = attributesMap.get("personalTitle");
            String givenName = attributesMap.get("givenName");
            String familyName = attributesMap.get("sn");
            if (title != null && givenName != null & familyName != null) {
                FullName fullName = entityCreator.getFullName();
                fullName.setTitle(title);
                fullName.setGivenName(givenName);
                fullName.setFamilyName(familyName);
                agent.setFullName(fullName);
            }
            String jobTitle = attributesMap.get("title");
            if (jobTitle == null) {
                jobTitle = "Staff Member";
            }
            String page = attributesMap.get("labeledURI");
            if (page != null) {
                version.getPages().add(page);
            }
            String nickName = attributesMap.get("pub-displayname");
            if (nickName != null) {
                version.getAlternatives().add(nickName);
            }
            version.setTitle(name);
            version.setDescription(name + " is a " + jobTitle + " in " + organisation + " at The University of Queensland");
            Date now = new Date();
            version.setUpdated(now);
            version.setType(AgentType.PERSON);
            version.getMboxes().add(mail);

            version.setParent(agent);
            agent.getVersions().add(version);
            version.getParent().setPublished(version);
            agent.setUpdated(now);

            agent.setUpdated(now);
            agent.setSource(systemSource);
            agent.getAuthors().add(agent);

            return agent;
        }
        return null;
    }
}
