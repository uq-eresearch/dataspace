package net.metadata.dataspace.model;

import java.util.Date;

/**
 * User: alabri
 * Date: 16/09/2010
 * Time: 2:55:57 PM
 */
public class Employee {
    private int id;
    private String name;
    private Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
