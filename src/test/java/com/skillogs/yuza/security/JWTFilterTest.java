package com.skillogs.yuza.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class JWTFilterTest {

    private JWTFilter filter;
    @MockBean
    private TokenProvider tokenProvider;


    @Before
    public void setup() {
        filter = new JWTFilter(tokenProvider);
    }

    @Test
    public void should_returns_unauthorized_when_no_authentification() throws IOException, ServletException {
        MockFilterChain mockChain = new MockFilterChain();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(tokenProvider.getAuthentication(req))
                .thenReturn(null);

        filter.doFilter(req, res, mockChain);

        assertThat(res.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void should_returns_ok_when_authentification() throws IOException, ServletException {
        MockFilterChain mockChain = new MockFilterChain();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        when(tokenProvider.getAuthentication(req))
                .thenReturn(new TestingAuthenticationToken("address@host.com", null, "SOME_ROLE"));

        filter.doFilter(req, res, mockChain);

        assertThat(res.getStatus(), is(HttpStatus.OK.value()));
    }
}
