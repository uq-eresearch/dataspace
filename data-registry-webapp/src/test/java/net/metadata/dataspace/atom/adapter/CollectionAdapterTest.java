package net.metadata.dataspace.atom.adapter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import net.metadata.dataspace.app.Constants;
import net.metadata.dataspace.app.NonProductionConstants;
import net.metadata.dataspace.atom.util.ClientHelper;
import net.metadata.dataspace.data.model.record.User;
import net.metadata.dataspace.data.model.record.User.Role;
import net.metadata.dataspace.servlets.RegistryServiceProviderServlet;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = NonProductionConstants.TEST_CONTEXT)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class CollectionAdapterTest {

	@Autowired
	private static Abdera abdera;

	@Autowired
	@Qualifier("collection")
	private org.apache.abdera.protocol.server.CollectionAdapter collectionAdapter;

	@BeforeClass
	public static void setupServlet() {
		AbderaServlet abderaServlet = new RegistryServiceProviderServlet();
		abdera = abderaServlet.getAbdera();
	}

	@Test
    @Transactional
	public void testGetEmptyFeed() throws IOException {
		RequestContext request = getMockRequest();
		when(request.getUri()).thenReturn(new IRI("http://example.test/collection.atom"));

		// Simulate a request, get a input stream and parse it
		ResponseContext response = collectionAdapter.getFeed(request);
		assertEquals(200, response.getStatus());
		assertEquals("OK", response.getStatusText());
		@SuppressWarnings("unchecked")
		Document<Feed> feedDoc = (Document<Feed>) getDocument(response);

		// There should be no entries returned
		assertEquals(0, feedDoc.getRoot().getEntries().size());
	}

	@Test
    @Transactional
	public void testCreateEntry() throws IOException, MimeTypeParseException {
		final String filename = "/files/post/new-collection.xml";
		RequestContext request = createEntryRequestFromFile(filename);
		when(request.getUri()).thenReturn(new IRI("http://example.test/collection/"));
		ResponseContext response;

		// Simulate a request
		response = collectionAdapter.postEntry(request);
		// Should fail as unauthorised
		assertEquals(401, response.getStatus());

		attachAuthenticatedUser(request);

		// Simulate a request
		response = collectionAdapter.postEntry(request);
		// Should create entity
		assertEquals("Entry not created: "+response.getStatusText(), 201, response.getStatus());

		// Get the created entity
		request = createEntryRequest();
		attachAuthenticatedUser(request);
		when(request.getUri()).thenReturn(response.getLocation());
		when(request.getAccept()).thenReturn(Constants.MIME_TYPE_ATOM_ENTRY);
		response = collectionAdapter.getEntry(request);

		// Should be successful
		assertEquals(200, response.getStatus());

		{
			@SuppressWarnings("unchecked")
			Document<Entry> entryDoc = (Document<Entry>) getDocument(response);
			assertEquals(1, entryDoc.getRoot().getLinks("working-copy").size());
			assertEquals("1", entryDoc.getRoot().getLinks("working-copy").get(0).getTitle());
			assertEquals(1, entryDoc.getRoot().getLinks(Constants.REL_IS_REFERENCED_BY).size());
		}
	}

	@Test
    @Transactional
	public void testUpdateEntry() throws IOException, MimeTypeParseException {
		final String postFile = "/files/post/new-collection.xml";
		RequestContext request;
		request = createEntryRequestFromFile(postFile);
		when(request.getUri()).thenReturn(new IRI("http://example.test/collection/"));
		ResponseContext response;

		// Create the entity
		attachAuthenticatedUser(request);
		response = collectionAdapter.postEntry(request);
		// Should create entity
		assertEquals("Entry not created: "+response.getStatusText(), 201, response.getStatus());

		final String putFile = "/files/put/update-collection.xml";
		request = createEntryRequestFromFile(putFile);
		when(request.getUri()).thenReturn(response.getLocation());

		// Simulate a request
		attachAuthenticatedUser(request);
		response = collectionAdapter.putEntry(request);
		// Should create entity
		assertEquals(200, response.getStatus());
		{
			@SuppressWarnings("unchecked")
			Document<Entry> entryDoc = (Document<Entry>) getDocument(response);
			assertEquals(1, entryDoc.getRoot().getLinks("working-copy").size());
			assertEquals("2", entryDoc.getRoot().getLinks("working-copy").get(0).getTitle());
			assertEquals(2, entryDoc.getRoot().getLinks(Constants.REL_IS_REFERENCED_BY).size());
		}

		// Get the updated entity
		request = createEntryRequest();
		attachAuthenticatedUser(request);
		when(request.getUri()).thenReturn(response.getLocation());
		when(request.getAccept()).thenReturn(Constants.MIME_TYPE_ATOM_ENTRY);
		response = collectionAdapter.getEntry(request);
		assertEquals(200, response.getStatus());
		{
			@SuppressWarnings("unchecked")
			Document<Entry> entryDoc = (Document<Entry>) getDocument(response);
			assertEquals(1, entryDoc.getRoot().getLinks("working-copy").size());
			assertEquals("2", entryDoc.getRoot().getLinks("working-copy").get(0).getTitle());
			assertEquals(2, entryDoc.getRoot().getLinks(Constants.REL_IS_REFERENCED_BY).size());
		}
	}

	protected void attachAuthenticatedUser(RequestContext request) {
		when(request.getAttribute(RequestContext.Scope.SESSION, Constants.SESSION_ATTRIBUTE_CURRENT_USER))
			.thenReturn(new User("testuser", Role.ADMIN));
	}

	protected RequestContext createEntryRequest() {
		RequestContext request = getMockRequest();
		Target target = mock(Target.class);
		when(request.getTarget()).thenReturn(target);
		when(target.getType()).thenReturn(TargetType.TYPE_ENTRY);
		return request;
	}


	protected RequestContext createEntryRequestFromFile(final String filename)
			throws FileNotFoundException, IOException, MimeTypeParseException {
		final Reader reader = new FileReader(
				ClientHelper.getFile(filename));

		RequestContext request = createEntryRequest();

		when(request.getContentType()).thenReturn(new MimeType(Constants.MIME_TYPE_ATOM_ENTRY));
		when(request.getReader()).thenReturn(reader);
		when(request.getDocument(any(Parser.class))).then(new Answer<Document<Entry>>() {
			@Override
			public Document<Entry> answer(InvocationOnMock invocation) throws Throwable {
				Parser parser = (Parser) invocation.getArguments()[0];
				return parser.parse(reader);
			}
		});
		return request;
	}

	protected RequestContext getMockRequest() {
		RequestContext request = mock(RequestContext.class);
		when(request.getAbdera()).thenReturn(abdera);
		return request;
	}

	protected Document<?> getDocument(ResponseContext response)
			throws IOException {
		StringWriter writer = new StringWriter();
		response.writeTo(writer);
		StringReader reader = new StringReader(writer.getBuffer().toString());
		return abdera.getParser().parse(reader);
	}



}
