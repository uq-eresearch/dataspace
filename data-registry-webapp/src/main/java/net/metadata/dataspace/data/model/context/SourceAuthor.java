package net.metadata.dataspace.data.model.context;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

@Embeddable
public class SourceAuthor {

	@Basic(optional=false)
	private String name;
	
	private String email;
	private String uri;

	public SourceAuthor() {}
	
	public SourceAuthor(String name, String email, URI uri) {
		setName(name);
		setEmail(email);
		setUri(uri);
	}
	
	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public URI getUri() {
		try {
			return uri == null ? null : new URI(uri);
		} catch (URISyntaxException e) {
			// Should never happen, as only valid URIs can be inserted
			return null;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUri(URI uri) {
		this.uri = (uri == null ? null : uri.toString());
	}
	

}
