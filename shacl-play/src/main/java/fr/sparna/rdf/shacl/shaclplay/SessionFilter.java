package fr.sparna.rdf.shacl.shaclplay;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFilter implements Filter {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(
			ServletRequest request,
			ServletResponse response,
			FilterChain chain)
	throws IOException, ServletException {
		 // Check type request.
        if (request instanceof HttpServletRequest) {
            // Cast back to HttpServletRequest.
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Parse HttpServletRequest.
            HttpServletRequest parsedRequest = filterRequest(httpRequest);

            // Continue with filter chain.
            chain.doFilter(parsedRequest, response);
        } else {
            // Not a HttpServletRequest.
            chain.doFilter(request, response);
        }
		
	}

	private HttpServletRequest filterRequest(HttpServletRequest request) {
		
		SessionData session = SessionData.get(request.getSession());
		if(session == null) {
			log.trace("No session data present. Will create it.");
			session = new SessionData();
			session.store(request.getSession());
			
			// set up Locale
			session.setUserLocale(request.getLocale());
		}
		
		if(request.getParameter("lang") != null) {
			log.trace("Detected 'lang' param. Will set a new user locale.");
			session.setUserLocale(new Locale(request.getParameter("lang")));
		}
		
		return request;
	}
	
}
