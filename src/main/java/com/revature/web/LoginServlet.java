package com.revature.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.service.LoginService;
import com.revature.service.LoginServiceImpl;
import com.revature.service.TokenServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private LoginService service;
    private ObjectMapper mapper;
    private final Logger logger = LogManager.getLogger(getClass());


    @Override
    public void init() throws ServletException {
        service = new LoginServiceImpl();
        mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logger.info("Inside LoginServlet");
        response.setContentType("application/json");
        String token = service.attemptAuthentication(request, response);
        if (token == null) {
            response.getOutputStream().write(mapper.writeValueAsBytes(Collections.singletonMap("message", "Invalid " +
                    "Credentials")));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.getOutputStream().write(mapper.writeValueAsBytes(Collections.singletonMap("token",
                TokenServiceImpl.TOKEN_PREFIX + token)));
    }

}
