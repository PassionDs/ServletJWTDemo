package com.revature.service;

import com.revature.utils.CurrentUserThreadLocal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServiceImpl implements TestService {

    private final TokenService tokenService = TokenServiceImpl.getInstance();
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    public Object process(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Inside TestServiceImpl");
        // 获取当前登录用户
        return CurrentUserThreadLocal.get();
    }

}
