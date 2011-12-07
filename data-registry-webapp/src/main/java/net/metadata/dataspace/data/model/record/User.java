package net.metadata.dataspace.data.model.record;

import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.EnumType.STRING;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 3:02:42 PM
 */
@Entity(name = "AppUser")
public class User implements Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = 7038969101379646565L;

	public enum Role {
	    ADMIN,
	    USER
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String displayName;

    @NotNull
    @Column(unique = true)
    private String email;

    @Enumerated(STRING)
    private Role role;

    public User() {
    }

    public User(String username) {
        this.username = username;
        this.role = Role.USER;
    }

    public User(String username, String displayName, String email) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.role = Role.USER;
    }

    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AbstractRecordEntity)) {
            return false;
        }
        User other = (User) obj;
        return getId().equals(other.getId());
    }
}
