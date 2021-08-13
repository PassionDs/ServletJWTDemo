package com.revature.dao;

import com.revature.data.BasicH2ConnectionPool;
import com.revature.data.ConnectionPool;
import com.revature.model.AuthenticatedUser;
import com.revature.model.LoginForm;
import com.revature.model.UserDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AuthenticationDaoImpl implements AuthenticationDao {

    private static final AuthenticationDao INSTANCE = new AuthenticationDaoImpl();

    private final Logger logger = LogManager.getLogger(getClass());
    private final ConnectionPool connectionPool = BasicH2ConnectionPool.getInstance();

    private AuthenticationDaoImpl() {
    }

    public static AuthenticationDao getInstance() {
        return INSTANCE;
    }

    @Override
    public UserDetails attemptLogin(LoginForm form) {
        logger.info("Attempting to authenticate user {} at {}", form.getUsername(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")));
        AuthenticatedUser user = null;
        try (Connection conn = connectionPool.getConnection()) {
            // 根据用户名和密码查询数据库中用户信息
            PreparedStatement stmt = conn.prepareStatement("select * from users where username = ? AND password = ?");
            stmt.setString(1, form.getUsername());
            stmt.setString(2, form.getPassword());
            ResultSet rs = stmt.executeQuery();
            // 如果能够查询到就放入到user变量中
            while (rs.next()) {
                user = new AuthenticatedUser(rs.getString("username"), rs.getString("email"),
						Arrays.stream(rs.getString("roles").split(",")).collect(Collectors.toList()));
            }
        } catch (SQLException e) {
            logger.error("SQL Exception caught: {}", e.getLocalizedMessage());
        }
        return user;
    }

}
