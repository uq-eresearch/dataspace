package net.metadata.dataspace.data.model.context;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import net.metadata.dataspace.data.model.record.Agent;

/**
 * An mbox can have only ever have one owner at a time, although it may have
 * multiple owners throughout its lifetime.
 *
 * @author Tim Dettrick
 */
@Entity
public class Mbox {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
	private Agent owner;

    @NotNull
    @Column(unique = true)
	private String emailAddress;

    public Mbox() {}

    public Mbox(InternetAddress emailAddress) {
    	this.setEmailAddress(emailAddress);
    }

    public Mbox(String emailAddress) throws AddressException {
    	this.setEmailAddress(emailAddress);
    }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Agent getOwner() {
		return owner;
	}

	public void setOwner(Agent owner) {
		this.owner = owner;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(InternetAddress address) {
		this.emailAddress = address.getAddress();
	}

	public void setEmailAddress(String emailAddress) throws AddressException {
		setEmailAddress(new InternetAddress(
				emailAddress.toLowerCase()));
	}

	public InternetAddress toInternetAddress() {
		if (emailAddress == null) {
			return null;
		}
		InternetAddress address;
		try {
			address = new InternetAddress(emailAddress);
		} catch (AddressException e) {
			// Address should already have been checked!
			throw new RuntimeException(e);
		}
		if (this.getOwner() != null) {
			try {
				address.setPersonal(this.getOwner().getTitle());
			} catch (UnsupportedEncodingException e) {
				// Just complain to the log - we didn't really need it
				e.printStackTrace();
			}
		}
		return address;
	}

	public URI toUri() {
		try {
			return new URI("mailto:"+this.toInternetAddress().getAddress());
		} catch (URISyntaxException e) {
			// This should never happen
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return toInternetAddress() == null ?
				"" : toInternetAddress().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mbox other = (Mbox) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		return true;
	}

}
