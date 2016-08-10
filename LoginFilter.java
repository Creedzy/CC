package hello;

import java.io.IOException;
import java.util.Enumeration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import hello.User.Role;

//@WebFilter("/User/*")
public class LoginFilter implements Filter {
	
	FilterConfig filterConfig = null;
    @Override
    public void init(FilterConfig config) throws ServletException {
       this.filterConfig = config;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
       
        LoginBean LB = (LoginBean)((HttpServletRequest)request).getSession().getAttribute("LoginBean"); 
        String path = request.getRequestURI().substring(request.getContextPath().length());
        /*System.out.println("Session is: "+session);
        
        System.out.println(path);
        System.out.println("Session attribute user is: " + session.getAttribute("user"));
        System.out.println("LB is: " + LB);
        System.out.println("Logged in is: "+ LB.getlogged());*/
        if (path.startsWith("/Admin/") && !LB.getUser().hasRole("ADMIN")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        if(LB == null || !LB.getlogged() ) {
        	response.sendRedirect(request.getContextPath() + "/login.xhtml");
        } else {
        	chain.doFilter(req, res); 
        	
        }
        	
     
    /*  if (session == null ) {
    	   System.out.println(session);
    	   System.out.println(session.getAttribute("username"));
    	   System.out.println(session.isNew());
    	   Enumeration e = session.getAttributeNames();
           while (e.hasMoreElements())
           {
             String attr = (String)e.nextElement();
             System.err.println("      attr  = "+ attr);
             Object value = session.getValue(attr);
             System.err.println("      value = "+ value);
           }
            response.sendRedirect(request.getContextPath() + "/login.xhtml"); // No logged-in user found, so redirect to login page.
        } else {
            chain.doFilter(req, res); // Logged-in user found, so just continue request.
            
        }*/
    }

    @Override
    public void destroy() {
        // If you have assigned any expensive resources as field of
        // this Filter class, then you could clean/close them here.
    }

}