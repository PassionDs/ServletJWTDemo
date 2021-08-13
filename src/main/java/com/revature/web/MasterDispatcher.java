package com.revature.web;

import com.revature.service.TestService;
import com.revature.service.TestServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MasterDispatcher {

    private static final TestService service = new TestServiceImpl();
    private static final Logger logger = LogManager.getLogger(MasterDispatcher.class);

    private MasterDispatcher() {
    }

    public static Object process(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Inside MasterDispatcher");
        if (request.getRequestURI().contains("test")) {
            return service.process(request, response);
        } else {
            return "Not yet implemented";
        }
    }
}
