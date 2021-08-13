package com.revature.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.service.TokenService;
import com.revature.service.TokenServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter implements Filter {

    private ObjectMapper mapper;
    private final Logger logger = LogManager.getLogger(getClass());
    private final TokenService tokenService = TokenServiceImpl.getInstance();

    public JwtAuthenticationFilter() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        logger.info("Attempting to find JWT token");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        final String token = httpRequest.getHeader("Authorization");


        if (token == null) {
            logger.info("Authentication failed");
            response.resetBuffer();
            response.setContentType("application/json");
            response.getOutputStream().write(mapper.writeValueAsBytes(Collections.singletonMap("message", "You must " +
                    "log in to view this resource")));
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!tokenService.validateToken(token)) {
            logger.info("Authentication failed: Token is invalid");
            response.setContentType("application/json");
            response.getOutputStream().write(mapper.writeValueAsBytes(Collections.singletonMap("message", "You must " +
                    "log in generate a new token")));
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        logger.info("JWT found and is valid! Passing request on");
        // pass the request along the filter chain
        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        mapper = new ObjectMapper();
    }

}
