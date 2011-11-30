package net.metadata.dataspace.data.model;

import javax.mail.internet.AddressException;

import net.metadata.dataspace.data.model.context.Mbox;

import static org.junit.Assert.*;
import org.junit.Test;


public class MboxTest {

	/**
	 * RFC 5321 discourages case-sensitive email addresses, so we're going to
	 * assert that our system should lower-case everything. (It might break
	 * something, but it will lead to much less confusion.)
	 */
	@Test
	public void testCaseInsensitive() {
		String address = "MyNewEmailAddress@GMail.com";
		Mbox mbox = new Mbox();
		try {
			mbox.setEmailAddress(address);
		} catch (AddressException e) {
			fail(address+" should be a valid email address");
		}
		assertEquals(address.toLowerCase(),mbox.getEmailAddress());
	}

}
