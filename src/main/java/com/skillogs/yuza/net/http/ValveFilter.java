package com.skillogs.yuza.net.http;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ValveFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValveFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            doFilter((HttpServletRequest) req, (HttpServletResponse) res, chain);
        }
    }

    private void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        LOGGER.info("Received {} {} from {} ({})", req.getMethod(), req.getRequestURI(), req.getRemoteHost(), req.getHeader("User-Agent"));
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            LOGGER.info("Processed {} {} ({} ms) {}", req.getMethod(), req.getRequestURI(), System.currentTimeMillis() - start, res.getStatus());
        }
    }

    @Override
    public void init(FilterConfig config) {
        LOGGER.info("Created HTTP valve logging filter");
    }

    @Override
    public void destroy() {}
}
