package com.management.librarymanagementcrudsystem.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.IOException;

@Component
@Order(1)
public class ApiKeyAuthenticationFilter implements Filter {

    private final ObjectMapper objectMapper;

    public ApiKeyAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Value("${app.security.api-key}")
    private String expectedApiKey;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestURI = httpServletRequest.getRequestURI();

        boolean isPublicApi = requestURI.equals("/api/library/Books")
                || requestURI.startsWith("/api/library/id/")
                || requestURI.startsWith("/api/library/isbn/");

        if(isPublicApi){
            //Allow Public request to continue
            chain.doFilter(request, response);
            return;
        }

        //Read Api Key From the request Header.
        String provideApiKey = httpServletRequest.getHeader("X-API-KEY");

        //Check whether the api key exists and matches
        if(provideApiKey == null || !expectedApiKey.equals(provideApiKey)){

            //Reject the request
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            FilterErrorResponse errorResponse = new FilterErrorResponse(403,
                    "Forbidden",
                    "Unauthorized access..", requestURI);

            objectMapper.writeValue(httpServletResponse.getOutputStream(),errorResponse);
            httpServletResponse.getWriter().flush();
            return;
        }
        chain.doFilter(request,response);

    }

}
