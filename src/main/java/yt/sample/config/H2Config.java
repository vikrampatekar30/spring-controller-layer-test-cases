package yt.sample.config;

import org.h2.server.web.JakartaWebServlet;
import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.Servlet;

@Configuration
public class H2Config {

	@Bean
	public ServletRegistrationBean<GenericServlet> h2servletRegistration() {
	    ServletRegistrationBean registration = new ServletRegistrationBean(new JakartaWebServlet());
	    registration.addUrlMappings("/console/*");
	    return registration;
	}
}
