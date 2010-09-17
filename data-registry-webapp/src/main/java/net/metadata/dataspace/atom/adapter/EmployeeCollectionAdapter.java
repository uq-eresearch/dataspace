package net.metadata.dataspace.atom.adapter;

import net.metadata.dataspace.model.Employee;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: alabri
 * Date: 16/09/2010
 * Time: 2:58:00 PM
 */
public class EmployeeCollectionAdapter extends AbstractEntityCollectionAdapter<Employee> {
    private Map<Integer, Employee> employees = new HashMap<Integer, Employee>();
    private static final String ID_PREFIX = "tag:acme.com,2007:employee:entry:";

    private AtomicInteger nextId = new AtomicInteger(1000);

    /**
     * A unique ID for this feed.
     */
    public String getId(RequestContext request) {
        return "tag:acme.com,2007:employee:feed";
    }

    /**
     * The title of our collection.
     */
    public String getTitle(RequestContext request) {
        return "Acme Employee Database";
    }

    /**
     * The author of this collection.
     */
    public String getAuthor(RequestContext request) {
        return "Acme Industries";
    }

    public String getId(Employee entry) {
        return ID_PREFIX + entry.getId();
    }

    public String getTitle(Employee entry) {
        return entry.getName();
    }

    public Date getUpdated(Employee entry) {
        return entry.getUpdated();
    }

    public List<Person> getAuthors(Employee entry, RequestContext request) throws ResponseContextException {
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName("Acme Industries");
        return Arrays.asList(author);
    }

    public Object getContent(Employee entry, RequestContext request) {
        Content content = request.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(entry.getName());
        return content;
    }

    @Override
    public Iterable<Employee> getEntries(RequestContext requestContext) throws ResponseContextException {
        return null;
    }

    public String getName(Employee entry) {
        return entry.getId() + "-" + Sanitizer.sanitize(entry.getName());
    }

    public Employee getEntry(String resourceName, RequestContext request) throws ResponseContextException {
        Integer id = getIdFromResourceName(resourceName);
        return employees.get(id);
    }

    public Employee postEntry(String title,
                              IRI id,
                              String summary,
                              Date updated,
                              List<Person> authors,
                              Content content,
                              RequestContext request) throws ResponseContextException {
        Employee employee = new Employee();
        employee.setName(content.getText().trim());
        employee.setId(nextId.getAndIncrement());
        employees.put(employee.getId(), employee);

        return employee;
    }

    public void putEntry(Employee employee,
                         String title,
                         Date updated,
                         List<Person> authors,
                         String summary,
                         Content content,
                         RequestContext request) throws ResponseContextException {
        employee.setName(content.getText().trim());
    }

    public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
        Integer id = getIdFromResourceName(resourceName);
        employees.remove(id);
    }


    private Integer getIdFromResourceName(String resourceName) throws ResponseContextException {
        int idx = resourceName.indexOf("-");
        if (idx == -1) {
            throw new ResponseContextException(404);
        }
        return new Integer(resourceName.substring(0, idx));
    }

}
