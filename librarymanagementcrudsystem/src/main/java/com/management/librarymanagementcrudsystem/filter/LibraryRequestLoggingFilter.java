package com.management.librarymanagementcrudsystem.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)

public class LibraryRequestLoggingFilter implements Filter {
    //Logger for recording request and response information
    private static final Logger logger = LoggerFactory.getLogger(LibraryRequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //Converting generic Servlet request&response to http request&response
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        //Read request info
        String method = httpRequest.getMethod();
        String uri= httpRequest.getRequestURI();
        System.out.println("Request Method: " +method);
        System.out.println("Request URI: " + uri);

        // Read the page query parameter
        String page = httpRequest.getParameter("page");
        // Read the size query parameter
        String size = httpRequest.getParameter("size");
        System.out.println("Page: " + page);
        System.out.println("Size: " + size);


        String ipAddress = httpRequest.getRemoteAddr();


        //Record the starting time.
        long startTime = System.nanoTime();

        try {
            //Continue the request to next Filter or servlet
            filterChain.doFilter(httpRequest, httpResponse);
        } finally {
            long endTime = System.nanoTime();

            long executionTime = (endTime - startTime)/1_000_000;

            logger.info("Incoming Request: {} {}", method,uri);
            logger.info("Client IP Address: {} ", ipAddress);
            //On Console
            System.out.println("IP Address: " + ipAddress);

            logger.info("Response Status: {}", httpResponse.getStatus());
            logger.info("Execution Time: {} ms", executionTime);


        }


    }
}
