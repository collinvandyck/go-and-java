package auth;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public interface UserDAO {

    @SqlQuery("SELECT u.id, u.email, u.password_hash, u.apikey, u.created_at, u.updated_at, u.name, u.id, u.admin, u.active from users u where u.apikey = :apiKey")
    @RegisterMapper(UserMapper.class)
    User getUserByApiKey(@Bind("apiKey") String apiKey);

    static class UserMapper implements ResultSetMapper<User> {
        @Override
        public User map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
            return new User(
                    rs.getString("id"),
                    rs.getString("email"),
                    new Date(rs.getTimestamp("created_at").getTime()),
                    new Date(rs.getTimestamp("updated_at").getTime()),
                    rs.getString("name"),
                    rs.getBoolean("admin"),
                    rs.getBoolean("active"));
        }

    }

}
