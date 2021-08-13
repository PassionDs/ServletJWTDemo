package com.revature.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dao.AuthenticationDao;
import com.revature.dao.AuthenticationDaoImpl;
import com.revature.model.LoginForm;
import com.revature.model.UserDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServiceImpl implements LoginService {

    private final TokenService service = TokenServiceImpl.getInstance();
    private final AuthenticationDao dao = AuthenticationDaoImpl.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    public String attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Inside LoginServiceImpl");
        LoginForm form = null;
        try {
            form = mapper.readValue(request.getInputStream(), LoginForm.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final UserDetails details = dao.attemptLogin(form);
        if (details != null) {
            logger.info("Authentication success! Return token");
            return service.generateToken(details);
        }
        return null;
    }

}
