package auth;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.Database;
import com.yammer.dropwizard.db.DatabaseFactory;

public class AuthService extends Service<AuthConfiguration> {
    public static void main(String[] args) throws Exception {
        new AuthService().run(args);
    }

    public AuthService() {
        super("auth-service");
    }

    @Override
    protected void initialize(AuthConfiguration config, Environment environment) throws Exception {
        final DatabaseFactory factory = new DatabaseFactory(environment);
        final Database db = factory.build(config.getDatabaseConfiguration(), "postgresql");

        final UserDAO userDAO = db.onDemand(UserDAO.class);
        environment.addResource(new AuthResource(userDAO));
    }
}
