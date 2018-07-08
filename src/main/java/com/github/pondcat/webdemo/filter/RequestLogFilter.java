package com.github.pondcat.webdemo.filter;

import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

@WebFilter(filterName = "requestLogFilter", urlPatterns = {"/api/*", "/openapi/*"})
public class RequestLogFilter extends OncePerRequestFilter {

    private static final AtomicInteger atomInt = new AtomicInteger(0);

    protected String getMessagePayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    return "[unknown]";
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean includePayload = isIncludePayload(request);

        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        new InputStreamReader(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        }, "utf-8").read();
        if (includePayload && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request);
        }

        MDC.put("traceId", Integer.toString(atomInt.get())); /// 多机部署时不能用本地自增
        /// MDC.put("spanId", ""); 单服务时用不到

        if (isFirstRequest && logger.isInfoEnabled()) {
//			logger.info(createMessage(requestToUse, includePayload));
        }
        try {
            filterChain.doFilter(requestToUse, response);
        } catch (Throwable ex) {
            logger.error("", ex);
        } finally {
            MDC.clear();
        }
    }

    protected String createMessage(HttpServletRequest request, boolean includePayload) {
        StringBuilder msg = new StringBuilder();
        msg.append("uri=").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        if (includePayload) {
            String payload = getMessagePayload(request);
            if (payload != null) {
                msg.append(";payload=").append(payload);
            }
        }

        return msg.toString();
    }

    private boolean isIncludePayload(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        contentType = contentType.toLowerCase();
        return contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) || contentType.startsWith(MediaType.APPLICATION_XML_VALUE);
    }

}
