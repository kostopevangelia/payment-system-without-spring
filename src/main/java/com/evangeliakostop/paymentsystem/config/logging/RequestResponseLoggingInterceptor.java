package com.evangeliakostop.paymentsystem.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Enumeration;

public class RequestResponseLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Wrapping is done by filter, so just return true here
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws IOException {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            String controllerName = method.getBeanType().getSimpleName();
            String methodName = method.getMethod().getName();

            ContentCachingRequestWrapper wrappedRequest = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
            String requestBody = wrappedRequest != null
                    ? new String(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding())
                    : "";

            String clientIp = getClientIpAddress(request);
            String headers = getHeaders(request);

            log.info("➡️ Entry to service from {} and method {}: Client IP: {}, Headers: {}, Request is: {}",
                    controllerName, methodName, clientIp, headers, requestBody);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws IOException {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            String controllerName = method.getBeanType().getSimpleName();
            String methodName = method.getMethod().getName();

            ContentCachingResponseWrapper wrappedResponse = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            String responseBody = wrappedResponse != null
                    ? new String(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding())
                    : "";

            log.info("✅ Exiting service from {} and method {}: Response is: {}", controllerName, methodName, responseBody);

            if (wrappedResponse != null) {
                wrappedResponse.copyBodyToResponse(); // Very important!
            }
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            builder.append(name).append(": ").append(value).append("; ");
        }

        return builder.toString().trim();
    }
}


