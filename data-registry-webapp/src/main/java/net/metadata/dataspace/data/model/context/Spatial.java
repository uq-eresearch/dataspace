package net.metadata.dataspace.data.model.context;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

@Embeddable
public class Spatial {
	
	@Basic(optional=false)
	private String name;
	
	@Basic(optional=false)
	private String locationUri;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URI getLocation() {
		try {
			return new URI(locationUri);
		} catch (URISyntaxException e) {
			// This should never happen, because we always insert a valid URI
			return null;
		}
	}

	public void setLocation(URI location) {
		this.locationUri = location.toString();
	}
	

}
