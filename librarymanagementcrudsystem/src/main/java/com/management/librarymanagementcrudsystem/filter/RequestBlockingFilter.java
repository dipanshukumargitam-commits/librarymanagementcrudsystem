//package com.management.librarymanagementcrudsystem.filter;
//
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import tools.jackson.databind.ObjectMapper;
//
//import java.io.IOException;
//
//@Component
//@Order(3)
//public class RequestBlockingFilter implements Filter {
//
//    private final ObjectMapper objectMapper;
//
//    public RequestBlockingFilter(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest,
//                        ServletResponse servletResponse,
//                         FilterChain filterChain) throws IOException, ServletException {
//
//        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
//
//        String method = httpRequest.getMethod();
//        String url= httpRequest.getRequestURI();
//
//        if ("DELETE".equalsIgnoreCase(method)) {
//            httpResponse.setStatus(httpResponse.SC_FORBIDDEN);
//            httpResponse.setContentType("application/json");
////            httpResponse.getWriter().write(
////                    "{\"message\":\"DELETE requests are blocked for this demonstration.\"}"
////            );
//            httpResponse.setCharacterEncoding("UTF-8");
//            FilterErrorResponse errorResponse = new FilterErrorResponse(403,
//                    "Forbidden",
//                    "DELETE requests are blocked for this demonstration.", url);
//
//           objectMapper.writeValue(httpResponse.getOutputStream(),errorResponse);
//            httpResponse.getWriter().flush();
//            return;
//        }
//        else if("POST".equalsIgnoreCase(method)) {
//            httpResponse.setStatus(httpResponse.SC_FORBIDDEN);
//            httpResponse.setContentType("application/json");
//            httpResponse.setCharacterEncoding("UTF-8");
//            FilterErrorResponse errorResponse = new FilterErrorResponse(403,
//                    "Forbidden",
//                    "CREATE requests are blocked for this demonstration.", url);
//
//            objectMapper.writeValue(httpResponse.getOutputStream(),errorResponse);
//            httpResponse.getWriter().flush();
//            return;
//        }
//        else if("PATCH".equalsIgnoreCase(method)) {
//            httpResponse.setStatus(httpResponse.SC_FORBIDDEN);
//            httpResponse.setContentType("application/json");
//            httpResponse.setCharacterEncoding("UTF-8");
//            FilterErrorResponse errorResponse = new FilterErrorResponse(403,
//                    "Forbidden",
//                    "UPDATE requests are blocked for this demonstration.", url);
//
//            objectMapper.writeValue(httpResponse.getOutputStream(),errorResponse);
//            httpResponse.getWriter().flush();
//            return;
//        }
//
//        filterChain.doFilter(httpRequest, httpResponse);
//    }
//}
