package auth;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.sun.jersey.api.core.HttpContext;
import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("authenticate")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private final UserDAO userDAO;

    public AuthResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    public User authenticate(@Context HttpContext context) {
        final String apiKey = getApiKeyFromAuthorizationHeader(context);
        final User user = userDAO.getUserByApiKey(apiKey);
        return user;
    }

    private String getApiKeyFromAuthorizationHeader(HttpContext context) {
        final String authorization = context.getRequest().getHeaderValue("Authorization");
        if (Strings.isNullOrEmpty(authorization)) {
            throw new WebApplicationException(Response.status(BAD_REQUEST).entity("Missing auth header").build());
        }
        final String[] pieces = authorization.split(" ");
        if (pieces.length != 2) {
            throw new WebApplicationException(Response.status(BAD_REQUEST).entity("Bad auth header").build());
        }
        if ("Basic".equals(pieces[0])) {
            return new String(Base64.decodeBase64(pieces[1]), Charsets.UTF_8);
        }
        return pieces[1];
    }


}
