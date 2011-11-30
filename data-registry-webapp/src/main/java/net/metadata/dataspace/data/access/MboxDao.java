package net.metadata.dataspace.data.access;

import javax.mail.internet.InternetAddress;

import au.edu.uq.itee.maenad.dataaccess.Dao;

import net.metadata.dataspace.data.model.context.Mbox;

public interface MboxDao extends Dao<Mbox> {

    Mbox getByEmail(InternetAddress email);

}
