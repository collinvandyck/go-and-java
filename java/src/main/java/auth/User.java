package auth;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.util.ISO8601DateFormat;

import java.text.DateFormat;
import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class User {
    private static final DateFormat ISO_8601_FORMATTER = new ISO8601DateFormat();

    public static String dateToISO8601Format(Date date) {
        if (date == null) {
            return null;
        }
        return ISO_8601_FORMATTER.format(date);
    }

    private String id;
    private String email;
    @JsonIgnore private Date createdAt;
    @JsonIgnore private Date updatedAt;
    private String name;
    private boolean admin;
    @JsonIgnore private boolean active;

    public User(String id, String email, Date createdAt, Date updatedAt, String name, boolean admin, boolean active) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.admin = admin;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("created_at")
    public String getCreatedAtISO8601() {
        return dateToISO8601Format(getCreatedAt());
    }

    @JsonProperty("updated_at")
    public String getUpdatedAtISO8601() {
        return dateToISO8601Format(getUpdatedAt());
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isActive() {
        return active;
    }

}
